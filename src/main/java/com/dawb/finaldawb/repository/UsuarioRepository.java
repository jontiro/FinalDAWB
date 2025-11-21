package com.dawb.finaldawb.repository;

import com.dawb.finaldawb.domain.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class UsuarioRepository {

    // Inyección del EntityManager para interactuar con JPA
    @PersistenceContext
    private EntityManager em;

    // --- Operaciones CRUD Básicas ---

    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            em.persist(usuario); // INSERT
            return usuario;
        } else {
            return em.merge(usuario); // UPDATE
        }
    }

    public Optional<Usuario> findById(Long id) {
        return Optional.ofNullable(em.find(Usuario.class, id));
    }

    public List<Usuario> findAll() {
        return em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
    }

    public void delete(Usuario usuario) {
        em.remove(em.contains(usuario) ? usuario : em.merge(usuario));
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