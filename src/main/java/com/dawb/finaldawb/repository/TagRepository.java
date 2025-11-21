package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Optional;

@Transactional
public class TagRepository {

    @PersistenceContext
    private EntityManager em;

    public Tag save(Tag tag) {
        if (tag.getId() == null) {
            em.persist(tag);
            return tag;
        } else {
            return em.merge(tag);
        }
    }

    public Optional<Tag> findById(Long id) {
        return Optional.ofNullable(em.find(Tag.class, id));
    }

    // Buscar un Tag por su nombre (UNIQUE KEY)
    public Optional<Tag> findByNombre(String nombre) {
        try {
            Tag tag = em.createQuery("SELECT t FROM Tag t WHERE t.nombre = :nombre", Tag.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();
            return Optional.of(tag);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}