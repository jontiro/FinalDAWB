package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Optional;

@Transactional
public class RoleRepository {

    @PersistenceContext
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
}