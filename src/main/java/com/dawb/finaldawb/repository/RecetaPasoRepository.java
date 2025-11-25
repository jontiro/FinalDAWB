package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.RecetaPaso;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.Optional;

@ApplicationScoped
public class RecetaPasoRepository {

    @Inject
    private EntityManager em;

    public RecetaPaso save(RecetaPaso paso) {
        if (paso.getId() == null) {
            em.persist(paso);
            return paso;
        } else {
            return em.merge(paso);
        }
    }

    public Optional<RecetaPaso> findById(Long id) {
        return Optional.ofNullable(em.find(RecetaPaso.class, id));
    }

    public void delete(RecetaPaso recetaPaso) {
        em.remove(em.contains(recetaPaso) ? recetaPaso : em.merge(recetaPaso));
    }
}