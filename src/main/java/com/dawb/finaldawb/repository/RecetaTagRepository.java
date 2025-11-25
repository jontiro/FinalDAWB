package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.RecetaTag;
import com.dawb.finaldawb.domain.RecetaTagId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.Optional;

@ApplicationScoped
public class RecetaTagRepository {

    @Inject
    private EntityManager em;

    public RecetaTag save(RecetaTag recetaTag) {
        if (recetaTag.getId() == null) {
            em.persist(recetaTag);
            return recetaTag;
        } else {
            return em.merge(recetaTag);
        }
    }

    public Optional<RecetaTag> findById(RecetaTagId id) {
        return Optional.ofNullable(em.find(RecetaTag.class, id));
    }

    public void delete(RecetaTag recetaTag) {
        em.remove(em.contains(recetaTag) ? recetaTag : em.merge(recetaTag));
    }
}