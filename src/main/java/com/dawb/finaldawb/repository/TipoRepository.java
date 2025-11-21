package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.Tipo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class TipoRepository {

    @PersistenceContext
    private EntityManager em;

    public Tipo save(Tipo tipo) {
        if (tipo.getId() == null) {
            em.persist(tipo);
            return tipo;
        } else {
            return em.merge(tipo);
        }
    }

    public Optional<Tipo> findById(Long id) {
        return Optional.ofNullable(em.find(Tipo.class, id));
    }

    public List<Tipo> findAll() {
        return em.createQuery("SELECT t FROM Tipo t", Tipo.class).getResultList();
    }

    public void delete(Tipo tipo) {
        em.remove(em.contains(tipo) ? tipo : em.merge(tipo));
    }

    /**
     * Busca un Tipo por su descripción (la clave de negocio).
     */
    public Optional<Tipo> findByDescripcion(String descripcion) {
        try {
            Tipo tipo = em.createQuery("SELECT t FROM Tipo t WHERE t.descripcion = :desc", Tipo.class)
                    .setParameter("desc", descripcion)
                    .getSingleResult();
            return Optional.of(tipo);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}