package com.dawb.finaldawb.service;

import com.dawb.finaldawb.domain.Objeto;
import com.dawb.finaldawb.repository.ObjetoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class ObjetoService {

    private final ObjetoRepository objetoRepository;

    // Inyección por constructor
    @Inject
    public ObjetoService(ObjetoRepository objetoRepository) {
        this.objetoRepository = objetoRepository;
    }

    /**
     * Obtiene todos los tipos de Objeto (Receta, Lugar, etc.).
     */
    public List<Objeto> findAll() {
        return objetoRepository.findAll();
    }

    /**
     * Busca un Objeto por su clave primaria (ID).
     */
    public Optional<Objeto> findById(Long id) {
        return objetoRepository.findById(id);
    }

    /**
     * Busca un Objeto por su descripción (clave única).
     */
    public Optional<Objeto> findByDescripcion(String descripcion) {
        return objetoRepository.findByDescripcion(descripcion);
    }

    /**
     * Crea un nuevo Objeto si no existe.
     * @param descripcion Descripción única del objeto (ej. "Receta").
     * @return El Objeto persistido o Optional.empty() si la descripción ya existe.
     */
    public Optional<Objeto> createObjeto(String descripcion) {
        // Regla de negocio: La descripción debe ser única
        if (objetoRepository.findByDescripcion(descripcion).isPresent()) {
            return Optional.empty();
        }

        Objeto nuevoObjeto = new Objeto();
        nuevoObjeto.setDescripcion(descripcion);

        return Optional.of(objetoRepository.save(nuevoObjeto));
    }

    /**
     * Elimina un Objeto por ID.
     * NOTA: Requiere permisos de administrador.
     * @param id ID del Objeto a eliminar.
     * @return true si fue eliminado.
     */
    public boolean deleteObjeto(Long id) {
        Optional<Objeto> objetoOpt = objetoRepository.findById(id);
        if (objetoOpt.isPresent()) {
            objetoRepository.delete(objetoOpt.get());
            return true;
        }
        return false;
    }
}