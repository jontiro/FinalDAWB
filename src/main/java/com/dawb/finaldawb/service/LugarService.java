package com.dawb.finaldawb.service;

import com.dawb.finaldawb.domain.Lugar;
import com.dawb.finaldawb.domain.Usuario;
import com.dawb.finaldawb.repository.LugarRepository;
import com.dawb.finaldawb.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LugarService {

    @Inject
    private EntityManager em;

    private LugarRepository lugarRepository;
    private UsuarioRepository usuarioRepository;

    // Constructor vacío requerido por CDI
    protected LugarService() {
    }

    // Inyección por constructor
    @Inject
    public LugarService(LugarRepository lugarRepository, UsuarioRepository usuarioRepository, EntityManager em) {
        this.lugarRepository = lugarRepository;
        this.usuarioRepository = usuarioRepository;
        this.em = em;
    }

    /**
     * Obtiene todos los lugares registrados.
     */
    public List<Lugar> findAll() {
        return lugarRepository.findAll();
    }

    /**
     * Obtiene un lugar por ID.
     */
    public Optional<Lugar> findById(Long id) {
        return lugarRepository.findById(id);
    }

    /**
     * Obtiene todos los lugares creados por un usuario específico.
     * @param autorId ID del usuario creador.
     */
    public List<Lugar> findAllByAutorId(Long autorId) {
        return usuarioRepository.findById(autorId)
                .map(lugarRepository::findByAutor)
                .orElseGet(List::of); // Devuelve una lista vacía si el usuario no existe
    }

    /**
     * Crea un nuevo lugar.
     * Requiere el ID del autor para enlazar el objeto Usuario.
     * @param lugar Objeto Lugar (sin ID ni autor asignado).
     * @param autorId ID del usuario creador.
     * @return El lugar persistido o Optional.empty() si el autor no existe.
     */
    public Optional<Lugar> createLugar(Lugar lugar, Long autorId) {
        em.getTransaction().begin();
        try {
            Optional<Usuario> autorOpt = usuarioRepository.findById(autorId);
            if (autorOpt.isEmpty()) {
                em.getTransaction().rollback();
                return Optional.empty();
            }

            lugar.setAutor(autorOpt.get());
            lugar.setId(null);

            Lugar savedLugar = lugarRepository.save(lugar);
            em.flush(); // Forzar generación del ID
            em.getTransaction().commit();

            return Optional.of(savedLugar);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Actualiza un lugar existente.
     * @param lugar Lugar con los datos actualizados (debe tener ID).
     * @param autorId ID del autor, se utiliza para verificar que el usuario tenga permisos.
     * @return El lugar actualizado o Optional.empty() si no existe o el autor no coincide.
     */
    public Optional<Lugar> updateLugar(Lugar lugar, Long autorId) {
        em.getTransaction().begin();
        try {
            Optional<Lugar> existingLugarOpt = lugarRepository.findById(lugar.getId());

            if (existingLugarOpt.isEmpty()) {
                em.getTransaction().rollback();
                return Optional.empty();
            }

            // Verificar si el usuario es el autor
            boolean isAuthor = existingLugarOpt.get().getAutor().getId().equals(autorId);

            // Verificar si el usuario es admin
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(autorId);
            boolean isAdmin = usuarioOpt.isPresent() &&
                            usuarioOpt.get().getRole() != null &&
                            "ADMIN".equals(usuarioOpt.get().getRole().getNombre());

            // Permitir actualizar si es el autor o si es admin
            if (!isAuthor && !isAdmin) {
                em.getTransaction().rollback();
                return Optional.empty();
            }

            Lugar existingLugar = existingLugarOpt.get();
            existingLugar.setNombre(lugar.getNombre());
            existingLugar.setDireccion(lugar.getDireccion());
            existingLugar.setCiudad(lugar.getCiudad());
            existingLugar.setPais(lugar.getPais());

            Lugar updatedLugar = lugarRepository.save(existingLugar);
            em.getTransaction().commit();

            return Optional.of(updatedLugar);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Elimina un lugar por ID, verificando que el autor sea el correcto o que sea admin.
     * @param id ID del lugar a eliminar.
     * @param autorId ID del usuario que intenta eliminarlo.
     * @return true si fue eliminado.
     */
    public boolean deleteLugar(Long id, Long autorId) {
        em.getTransaction().begin();
        try {
            Optional<Lugar> lugarOpt = lugarRepository.findById(id);

            if (lugarOpt.isEmpty()) {
                em.getTransaction().rollback();
                return false;
            }

            // Verificar si el usuario es el autor
            boolean isAuthor = lugarOpt.get().getAutor().getId().equals(autorId);

            // Verificar si el usuario es admin
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(autorId);
            boolean isAdmin = usuarioOpt.isPresent() &&
                            usuarioOpt.get().getRole() != null &&
                            "ADMIN".equals(usuarioOpt.get().getRole().getNombre());

            // Permitir eliminar si es el autor o si es admin
            if (!isAuthor && !isAdmin) {
                em.getTransaction().rollback();
                return false;
            }

            lugarRepository.delete(lugarOpt.get());
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }
}