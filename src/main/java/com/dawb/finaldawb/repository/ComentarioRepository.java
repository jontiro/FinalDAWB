package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.Comentario;
import com.dawb.finaldawb.domain.Objeto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class ComentarioRepository {

    @PersistenceContext
    private EntityManager em;

    public Comentario save(Comentario comentario) {
        if (comentario.getId() == null) {
            em.persist(comentario);
            return comentario;
        } else {
            return em.merge(comentario);
        }
    }

    public Optional<Comentario> findById(Long id) {
        return Optional.ofNullable(em.find(Comentario.class, id));
    }

    // Consulta personalizada: Buscar comentarios asociados a un tipo de Objeto (Receta, Lugar, etc.)
    public List<Comentario> findByObjeto(Objeto objeto) {
        return em.createQuery("SELECT c FROM Comentario c WHERE c.objeto = :objeto ORDER BY c.fechaCreacion DESC", Comentario.class)
                .setParameter("objeto", objeto)
                .getResultList();
    }
}