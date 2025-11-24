package com.dawb.finaldawb.service;

import com.dawb.finaldawb.domain.Tipo;
import com.dawb.finaldawb.repository.TipoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class TipoService {

    private TipoRepository tipoRepository;

    // Constructor vacío requerido por CDI para crear proxies
    protected TipoService() {
    }

    // Inyección por constructor
    @Inject
    public TipoService(TipoRepository tipoRepository) {
        this.tipoRepository = tipoRepository;
    }

    /**
     * Obtiene todos los tipos de recomendación (Articulo, Video, etc.).
     */
    public List<Tipo> findAll() {
        return tipoRepository.findAll();
    }

    /**
     * Busca un tipo por su clave primaria (ID).
     */
    public Optional<Tipo> findById(Long id) {
        return tipoRepository.findById(id);
    }

    /**
     * Crea un nuevo tipo de recomendación si no existe.
     * @param descripcion Descripción única del tipo (ej. "Articulo").
     * @return El Tipo persistido o Optional.empty() si la descripción ya existe.
     */
    public Optional<Tipo> createTipo(String descripcion) {
        // Regla de negocio: La descripción debe ser única
        if (tipoRepository.findByDescripcion(descripcion).isPresent()) {
            return Optional.empty();
        }

        Tipo nuevoTipo = new Tipo();
        nuevoTipo.setDescripcion(descripcion);

        return Optional.of(tipoRepository.save(nuevoTipo));
    }

    /**
     * Elimina un tipo por ID.
     * NOTA: La BD debería tener una restricción de clave foránea para evitar
     * borrar tipos que ya tienen recomendaciones asociadas.
     * @param id ID del tipo a eliminar.
     * @return true si fue eliminado.
     */
    public boolean deleteTipo(Long id) {
        Optional<Tipo> tipoOpt = tipoRepository.findById(id);
        if (tipoOpt.isPresent()) {
            tipoRepository.delete(tipoOpt.get());
            return true;
        }
        return false;
    }
}