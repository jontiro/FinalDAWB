package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.Recomendacion;
import com.dawb.finaldawb.domain.Tipo;
import com.dawb.finaldawb.domain.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RecomendacionRepository {

    @Inject
    private EntityManager em;

    // --- Operaciones CRUD Básicas ---

    public Recomendacion save(Recomendacion recomendacion) {
        if (recomendacion.getId() == null) {
            em.persist(recomendacion);
            em.flush(); // Forzar sincronización para obtener el ID generado
            return recomendacion;
        } else {
            return em.merge(recomendacion);
        }
    }

    public Optional<Recomendacion> findById(Long id) {
        return Optional.ofNullable(em.find(Recomendacion.class, id));
    }

    public List<Recomendacion> findAll() {
        // Ordena por la más reciente
        return em.createQuery("SELECT r FROM Recomendacion r ORDER BY r.fechaCreacion DESC", Recomendacion.class).getResultList();
    }

    public void delete(Recomendacion recomendacion) {
        em.remove(em.contains(recomendacion) ? recomendacion : em.merge(recomendacion));
    }

    // --- Consultas Personalizadas ---

    /**
     * Busca todas las recomendaciones de un autor específico.
     */
    public List<Recomendacion> findByAutor(Usuario autor) {
        return em.createQuery("SELECT r FROM Recomendacion r WHERE r.autor = :autor ORDER BY r.fechaCreacion DESC", Recomendacion.class)
                .setParameter("autor", autor)
                .getResultList();
    }

    /**
     * Busca todas las recomendaciones por un tipo específico.
     */
    public List<Recomendacion> findByTipo(Tipo tipo) {
        return em.createQuery("SELECT r FROM Recomendacion r WHERE r.tipo = :tipo ORDER BY r.fechaCreacion DESC", Recomendacion.class)
                .setParameter("tipo", tipo)
                .getResultList();
    }
}