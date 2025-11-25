package com.dawb.finaldawb.service;

import com.dawb.finaldawb.domain.Receta;
import com.dawb.finaldawb.domain.RecetaPaso;
import com.dawb.finaldawb.domain.Tag;
import com.dawb.finaldawb.domain.RecetaTag;
import com.dawb.finaldawb.domain.RecetaTagId;
import com.dawb.finaldawb.domain.Usuario;
import com.dawb.finaldawb.repository.RecetaRepository;
import com.dawb.finaldawb.repository.RecetaPasoRepository;
import com.dawb.finaldawb.repository.TagRepository;
import com.dawb.finaldawb.repository.RecetaTagRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RecetaService {

    @Inject
    private EntityManager em;

    // Campos sin final para permitir el constructor vacío
    private RecetaRepository recetaRepository;
    private RecetaPasoRepository pasoRepository;
    private TagRepository tagRepository;
    private RecetaTagRepository recetaTagRepository;

    // Constructor vacío requerido por CDI
    protected RecetaService() {
    }

    // Inyección por constructor
    @Inject
    public RecetaService(RecetaRepository recetaRepository,
                         RecetaPasoRepository pasoRepository,
                         TagRepository tagRepository,
                         RecetaTagRepository recetaTagRepository,
                         EntityManager em) {
        this.recetaRepository = recetaRepository;
        this.pasoRepository = pasoRepository;
        this.tagRepository = tagRepository;
        this.recetaTagRepository = recetaTagRepository;
        this.em = em;
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
     * @param tagNombres Lista de nombres de tags (opcional, para buscar/crear).
     * @param tagIds Lista de IDs de tags (opcional, recomendado).
     * @return La receta persistida.
     */
    public Receta createReceta(Receta receta, List<RecetaPaso> pasos, List<String> tagNombres, List<Long> tagIds) {
        // Iniciar transacción manual
        em.getTransaction().begin();
        try {
            // 1. Guardar la receta principal (esto genera el ID)
            Receta savedReceta = recetaRepository.save(receta);
            em.flush(); // Forzar generación del ID
            em.clear(); // Limpiar la sesión para evitar conflictos

            // 2. Gestionar los Pasos
            if (pasos != null && !pasos.isEmpty()) {
                // Recargar la receta en la nueva sesión
                savedReceta = em.find(Receta.class, savedReceta.getId());

                if (savedReceta.getPasos() == null) {
                    savedReceta.setPasos(new ArrayList<>());
                }
                for (RecetaPaso paso : pasos) {
                    paso.setReceta(savedReceta);
                    pasoRepository.save(paso);
                }
                em.flush();
                em.clear(); // Limpiar después de pasos
            }

            // 3. Recargar la receta una vez más
            savedReceta = em.find(Receta.class, savedReceta.getId());

            // 4. Gestionar los Tags - PRIORIDAD A tagIds
            if (savedReceta.getRecetaTags() == null) {
                savedReceta.setRecetaTags(new ArrayList<>());
            }

            // Opción A: Usar tagIds (recomendado)
            if (tagIds != null && !tagIds.isEmpty()) {
                for (Long tagId : tagIds) {
                    Tag tag = em.find(Tag.class, tagId);
                    if (tag != null) {
                        RecetaTagId recetaTagId = new RecetaTagId(savedReceta.getId(), tag.getId());
                        RecetaTag existingRecetaTag = em.find(RecetaTag.class, recetaTagId);

                        if (existingRecetaTag == null) {
                            RecetaTag recetaTag = new RecetaTag(savedReceta, tag);
                            em.persist(recetaTag);
                        }
                    }
                }
            }
            // Opción B: Usar tagNombres (solo si no hay tagIds)
            else if (tagNombres != null && !tagNombres.isEmpty()) {
                for (String nombre : tagNombres) {
                    // Buscar con query nativa para evitar autoflush
                    Tag tag = em.createQuery(
                        "SELECT t FROM Tag t WHERE t.nombre = :nombre", Tag.class)
                        .setParameter("nombre", nombre)
                        .getResultStream()
                        .findFirst()
                        .orElseGet(() -> {
                            Tag t = new Tag();
                            t.setNombre(nombre);
                            em.persist(t);
                            em.flush();
                            return t;
                        });

                    RecetaTagId recetaTagId = new RecetaTagId(savedReceta.getId(), tag.getId());
                    RecetaTag existingRecetaTag = em.find(RecetaTag.class, recetaTagId);

                    if (existingRecetaTag == null) {
                        RecetaTag recetaTag = new RecetaTag(savedReceta, tag);
                        em.persist(recetaTag);
                    }
                }
            }

            // 5. Commit de la transacción
            em.flush();
            em.getTransaction().commit();

            // 6. Devolver la receta completa (recargar para obtener todas las relaciones)
            return recetaRepository.findById(savedReceta.getId()).orElse(savedReceta);
        } catch (Exception e) {
            // Rollback en caso de error
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Elimina una receta por ID.
     * @param id ID de la receta.
     * @return true if deleted.
     */
    public boolean deleteReceta(Long id) {
        em.getTransaction().begin();
        try {
            Optional<Receta> recetaOpt = recetaRepository.findById(id);
            if (recetaOpt.isPresent()) {
                recetaRepository.delete(recetaOpt.get());
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

}