package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.Receta;
import com.dawb.finaldawb.domain.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RecetaRepository {

    @Inject
    private EntityManager em;

    // --- Operaciones CRUD Básicas ---

    public Receta save(Receta receta) {
        if (receta.getId() == null) {
            em.persist(receta);
            return receta;
        } else {
            return em.merge(receta);
        }
    }

    public Optional<Receta> findById(Long id) {
        return Optional.ofNullable(em.find(Receta.class, id));
    }

    public List<Receta> findAllPublicas() {
        // Consulta personalizada para obtener solo recetas públicas (privacidad = false)
        return em.createQuery("SELECT r FROM Receta r WHERE r.privacidad = false", Receta.class)
                .getResultList();
    }

    public void delete(Receta receta) {
        em.remove(em.contains(receta) ? receta : em.merge(receta));
    }

    // --- Consultas Personalizadas ---

    // Buscar todas las recetas de un creador específico
    public List<Receta> findByCreador(Usuario creador) {
        return em.createQuery("SELECT r FROM Receta r WHERE r.creador = :creador ORDER BY r.fechaCreacion DESC", Receta.class)
                .setParameter("creador", creador)
                .getResultList();
    }

    // Nota: La búsqueda avanzada por título/descripción/tags se implementaría en la capa Service,
    // pero el repositorio proporcionaría los métodos base de consulta.
}