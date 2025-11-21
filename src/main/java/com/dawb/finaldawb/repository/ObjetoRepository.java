package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.Objeto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class ObjetoRepository {

    @PersistenceContext
    private EntityManager em;

    public Objeto save(Objeto objeto) {
        if (objeto.getId() == null) {
            em.persist(objeto);
            return objeto;
        } else {
            return em.merge(objeto);
        }
    }

    public Optional<Objeto> findById(Long id) {
        return Optional.ofNullable(em.find(Objeto.class, id));
    }

    public List<Objeto> findAll() {
        return em.createQuery("SELECT o FROM Objeto o", Objeto.class).getResultList();
    }

    public void delete(Objeto objeto) {
        em.remove(em.contains(objeto) ? objeto : em.merge(objeto));
    }

    /**
     * Busca un Objeto por su descripción (la clave única de negocio).
     */
    public Optional<Objeto> findByDescripcion(String descripcion) {
        try {
            Objeto objeto = em.createQuery("SELECT o FROM Objeto o WHERE o.descripcion = :desc", Objeto.class)
                    .setParameter("desc", descripcion)
                    .getSingleResult();
            return Optional.of(objeto);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}