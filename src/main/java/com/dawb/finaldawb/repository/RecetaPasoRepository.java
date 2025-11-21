package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.RecetaPaso;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Optional;

@Transactional
public class RecetaPasoRepository {

    @PersistenceContext
    private EntityManager em;

    public RecetaPaso save(RecetaPaso recetaPaso) {
        if (recetaPaso.getId() == null) {
            em.persist(recetaPaso);
            return recetaPaso;
        } else {
            return em.merge(recetaPaso);
        }
    }

    public Optional<RecetaPaso> findById(Long id) {
        return Optional.ofNullable(em.find(RecetaPaso.class, id));
    }

    public void delete(RecetaPaso recetaPaso) {
        em.remove(em.contains(recetaPaso) ? recetaPaso : em.merge(recetaPaso));
    }
}