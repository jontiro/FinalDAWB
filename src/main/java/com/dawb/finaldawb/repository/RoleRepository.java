package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.Role;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

import java.util.Optional;

@ApplicationScoped
public class RoleRepository {

    @Inject
    private EntityManager em;

    public Optional<Role> findById(Long id) {
        return Optional.ofNullable(em.find(Role.class, id));
    }

    // Buscar un Role por su nombre (ej. "ADMIN")
    public Optional<Role> findByNombre(String nombre) {
        try {
            Role role = em.createQuery("SELECT r FROM Role r WHERE r.nombre = :nombre", Role.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();
            return Optional.of(role);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    // Guardar o actualizar un Role
    public Role save(Role role) {
        EntityTransaction tx = em.getTransaction();
        try {
            if (!tx.isActive()) tx.begin();
            if (role.getId() == null) {
                em.persist(role); // INSERT
                em.flush(); // Forzar sincronización para obtener el ID generado
            } else {
                role = em.merge(role); // UPDATE
            }
            tx.commit();
            return role;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}