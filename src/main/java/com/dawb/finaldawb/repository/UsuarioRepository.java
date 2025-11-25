package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UsuarioRepository {

    // Inyección del EntityManager para interactuar con JPA (RESOURCE_LOCAL)
    @Inject
    private EntityManager em;

    // --- Operaciones CRUD Básicas ---

    public Usuario save(Usuario usuario) {
        EntityTransaction tx = em.getTransaction();
        try {
            if (!tx.isActive()) tx.begin();
            if (usuario.getId() == null) {
                em.persist(usuario); // INSERT
            } else {
                usuario = em.merge(usuario); // UPDATE
            }
            tx.commit();
            return usuario;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public Optional<Usuario> findById(Long id) {
        return Optional.ofNullable(em.find(Usuario.class, id));
    }

    public List<Usuario> findAll() {
        return em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
    }

    public void delete(Usuario usuario) {
        EntityTransaction tx = em.getTransaction();
        try {
            if (!tx.isActive()) tx.begin();
            em.remove(em.contains(usuario) ? usuario : em.merge(usuario));
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    // --- Consultas Personalizadas (necesarias para la lógica de negocio) ---

    // Buscar por username (UNIQUE KEY)
    public Optional<Usuario> findByUsername(String username) {
        try {
            Usuario usuario = em.createQuery("SELECT u FROM Usuario u WHERE u.username = :username", Usuario.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(usuario);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    // Buscar por email (UNIQUE KEY)
    public Optional<Usuario> findByEmail(String email) {
        try {
            Usuario usuario = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(usuario);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}