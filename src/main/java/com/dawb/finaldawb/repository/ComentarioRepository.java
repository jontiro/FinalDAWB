package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.Comentario;
import com.dawb.finaldawb.domain.Objeto;
import com.dawb.finaldawb.domain.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class ComentarioRepository {

    @PersistenceContext
    private EntityManager em;

    // --- Operaciones CRUD Básicas ---

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

    // Metodo de eliminación, necesario para ComentarioService
    public void delete(Comentario comentario) {
        em.remove(em.contains(comentario) ? comentario : em.merge(comentario));
    }

    // --- Consultas Personalizadas ---

    /**
     * Busca todos los comentarios creados por un usuario específico.
     */
    public List<Comentario> findByAutor(Usuario autor) {
        return em.createQuery("SELECT c FROM Comentario c WHERE c.autor = :autor ORDER BY c.fechaCreacion DESC", Comentario.class)
                .setParameter("autor", autor)
                .getResultList();
    }

    /**
     * Busca comentarios asociados a un tipo de Objeto específico (Receta, Lugar, Recomendacion).
     */
    public List<Comentario> findByObjeto(Objeto objeto) {
        return em.createQuery("SELECT c FROM Comentario c WHERE c.objeto = :objeto ORDER BY c.fechaCreacion DESC", Comentario.class)
                .setParameter("objeto", objeto)
                .getResultList();
    }

    /**
     * Busca comentarios que necesiten ser moderados (moderado = false).
     */
    public List<Comentario> findPendientesModeracion() {
        return em.createQuery("SELECT c FROM Comentario c WHERE c.moderado = false ORDER BY c.fechaCreacion ASC", Comentario.class)
                .getResultList();
    }
}