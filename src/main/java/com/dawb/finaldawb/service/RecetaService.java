package com.dawb.finaldawb.service;

import com.dawb.finaldawb.domain.Receta;
import com.dawb.finaldawb.domain.RecetaPaso;
import com.dawb.finaldawb.domain.Tag;
import com.dawb.finaldawb.domain.RecetaTag;
import com.dawb.finaldawb.domain.Usuario;
import com.dawb.finaldawb.repository.RecetaRepository;
import com.dawb.finaldawb.repository.RecetaPasoRepository;
import com.dawb.finaldawb.repository.TagRepository;
import com.dawb.finaldawb.repository.RecetaTagRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class RecetaService {

    // 1. Los campos inyectados ahora son finales (final)
    private final RecetaRepository recetaRepository;
    private final RecetaPasoRepository pasoRepository;
    private final TagRepository tagRepository;
    private final RecetaTagRepository recetaTagRepository;

    // 2. CONSTRUCTOR CON ARGUMENTOS (@Inject en el constructor)
    @Inject
    public RecetaService(RecetaRepository recetaRepository,
                         RecetaPasoRepository pasoRepository,
                         TagRepository tagRepository,
                         RecetaTagRepository recetaTagRepository) {
        this.recetaRepository = recetaRepository;
        this.pasoRepository = pasoRepository;
        this.tagRepository = tagRepository;
        this.recetaTagRepository = recetaTagRepository;
    }

    /**
     * Obtiene una receta por ID.
     */
    public Optional<Receta> findById(Long id) {
        return recetaRepository.findById(id);
    }

    /**
     * Obtiene una lista de recetas públicas (para el feed principal).
     */
    public List<Receta> findAllPublicas() {
        return recetaRepository.findAllPublicas();
    }

    /**
     * Crea una nueva receta, incluyendo sus pasos y etiquetas.
     * @param receta La entidad Receta con los datos básicos.
     * @param pasos Lista de RecetaPaso.
     * @param tagNombres Lista de nombres de tags (ej. ["Vegano", "Rápido"]).
     * @return La receta persistida.
     */
    public Receta createReceta(Receta receta, List<RecetaPaso> pasos, List<String> tagNombres) {
        // 1. Guardar la receta principal (esto genera el ID)
        Receta savedReceta = recetaRepository.save(receta);

        // 2. Gestionar los Pasos
        if (pasos != null) {
            // Asegura que la lista de pasos esté inicializada
            savedReceta.setPasos(new ArrayList<>());
            for (RecetaPaso paso : pasos) {
                paso.setReceta(savedReceta); // Enlazar el paso a la receta guardada
                pasoRepository.save(paso);
                savedReceta.getPasos().add(paso);
            }
        }

        // 3. Gestionar los Tags y la Tabla de Unión (M:N)
        if (tagNombres != null && !tagNombres.isEmpty()) {
            List<RecetaTag> nuevaRecetaTags = new ArrayList<>();
            for (String nombre : tagNombres) {
                // A. Buscar el Tag existente o crear uno nuevo si no existe (lógica de negocio)
                Tag tag = tagRepository.findByNombre(nombre)
                        .orElseGet(() -> {
                            // Se asume que Tag.java tiene el constructor Tag(String nombre)
                            Tag t = new Tag();
                            t.setNombre(nombre);
                            return tagRepository.save(t);
                        });

                // B. Crear la entidad de unión RecetaTag
                RecetaTag recetaTag = new RecetaTag(savedReceta, tag);
                recetaTagRepository.save(recetaTag);
                nuevaRecetaTags.add(recetaTag);
            }
            savedReceta.setRecetaTags(nuevaRecetaTags);
        }

        // 4. Devolver la receta completa
        return savedReceta;
    }

    /**
     * Elimina una receta por ID.
     * @param id ID de la receta.
     * @return true if deleted.
     */
    public boolean deleteReceta(Long id) {
        Optional<Receta> recetaOpt = recetaRepository.findById(id);
        if (recetaOpt.isPresent()) {
            recetaRepository.delete(recetaOpt.get());
            return true;
        }
        return false;
    }

}