package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.RecetaTag;
import com.dawb.finaldawb.domain.RecetaTagId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Optional;

@Transactional
public class RecetaTagRepository {

    @PersistenceContext
    private EntityManager em;

    public RecetaTag save(RecetaTag recetaTag) {
        if (recetaTag.getId() == null || recetaTag.getId().getRecetaId() == null) {
            // Para entidades con claves compuestas, usamos persist directamente
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