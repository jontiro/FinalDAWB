package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.Lugar;
import com.dawb.finaldawb.domain.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LugarRepository {

    @Inject
    private EntityManager em;

    // --- Operaciones CRUD Básicas ---

    public Lugar save(Lugar lugar) {
        if (lugar.getId() == null) {
            em.persist(lugar); // INSERT
            em.flush(); // Forzar sincronización con BD para obtener el ID generado
            return lugar;
        } else {
            return em.merge(lugar); // UPDATE
        }
    }

    public Optional<Lugar> findById(Long id) {
        return Optional.ofNullable(em.find(Lugar.class, id));
    }

    public List<Lugar> findAll() {
        return em.createQuery("SELECT l FROM Lugar l ORDER BY l.fechaCreacion DESC", Lugar.class).getResultList();
    }

    public void delete(Lugar lugar) {
        em.remove(em.contains(lugar) ? lugar : em.merge(lugar));
    }

    // --- Consultas Personalizadas ---

    /**
     * Busca todos los lugares creados por un usuario específico.
     * @param autor El usuario creador.
     * @return Lista de lugares.
     */
    public List<Lugar> findByAutor(Usuario autor) {
        return em.createQuery("SELECT l FROM Lugar l WHERE l.autor = :autor ORDER BY l.fechaCreacion DESC", Lugar.class)
                .setParameter("autor", autor)
                .getResultList();
    }
}