package com.dawb.finaldawb.service;

import com.dawb.finaldawb.domain.Objeto;
import com.dawb.finaldawb.repository.ObjetoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ObjetoService {

    @Inject
    private EntityManager em;

    private ObjetoRepository objetoRepository;

    // Constructor vacío requerido por CDI
    protected ObjetoService() {
    }

    // Inyección por constructor
    @Inject
    public ObjetoService(ObjetoRepository objetoRepository, EntityManager em) {
        this.objetoRepository = objetoRepository;
        this.em = em;
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
        em.getTransaction().begin();
        try {
            // Regla de negocio: La descripción debe ser única
            if (objetoRepository.findByDescripcion(descripcion).isPresent()) {
                em.getTransaction().rollback();
                return Optional.empty();
            }

            Objeto nuevoObjeto = new Objeto();
            nuevoObjeto.setDescripcion(descripcion);

            Objeto savedObjeto = objetoRepository.save(nuevoObjeto);
            em.flush(); // Forzar generación del ID
            em.getTransaction().commit();

            return Optional.of(savedObjeto);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Elimina un Objeto por ID.
     * NOTA: Requiere permisos de administrador.
     * @param id ID del Objeto a eliminar.
     * @return true si fue eliminado.
     */
    public boolean deleteObjeto(Long id) {
        em.getTransaction().begin();
        try {
            Optional<Objeto> objetoOpt = objetoRepository.findById(id);
            if (objetoOpt.isPresent()) {
                objetoRepository.delete(objetoOpt.get());
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