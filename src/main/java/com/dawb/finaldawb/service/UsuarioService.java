package com.dawb.finaldawb.service;

import com.dawb.finaldawb.domain.EstadoUsuario;
import com.dawb.finaldawb.domain.Role;
import com.dawb.finaldawb.domain.Usuario;
import com.dawb.finaldawb.repository.RoleRepository;
import com.dawb.finaldawb.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional // Asegura que las operaciones de modificación se ejecuten en una transacción
public class UsuarioService {

    @Inject
    private UsuarioRepository usuarioRepository;

    @Inject
    private RoleRepository roleRepository;

    /**
     * Obtiene todos los usuarios registrados en el sistema.
     * Ideal para tareas de administración.
     * @return Lista de todos los usuarios.
     */
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por su clave primaria (ID).
     * @param id ID del usuario.
     * @return Optional que contiene el Usuario o está vacío.
     */
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Actualiza el perfil de un usuario existente.
     * Permite cambiar email y username, asegurando que no haya duplicados.
     * @param id ID del usuario a actualizar.
     * @param newUsername Nuevo nombre de usuario.
     * @param newEmail Nuevo correo electrónico.
     * @return El Usuario actualizado o Optional.empty() si el username/email ya existen en otro usuario.
     */
    public Optional<Usuario> updateProfile(Long id, String newUsername, String newEmail) {
        return usuarioRepository.findById(id).map(usuario -> {

            // 1. Validar unicidad del Username
            Optional<Usuario> existingUserByUsername = usuarioRepository.findByUsername(newUsername);
            if (existingUserByUsername.isPresent() && !existingUserByUsername.get().getId().equals(id)) {
                // Username ya existe y pertenece a otro usuario
                return null;
            }

            // 2. Validar unicidad del Email
            Optional<Usuario> existingUserByEmail = usuarioRepository.findByEmail(newEmail);
            if (existingUserByEmail.isPresent() && !existingUserByEmail.get().getId().equals(id)) {
                // Email ya existe y pertenece a otro usuario
                return null;
            }

            // 3. Aplicar cambios y guardar
            usuario.setUsername(newUsername);
            usuario.setEmail(newEmail);
            return usuarioRepository.save(usuario);
        });
        // Si el .map devuelve null (debido a la validación), el .map final lo convierte a Optional.empty()
    }

    /**
     * Cambia el rol de un usuario. Requiere permisos administrativos.
     * @param userId ID del usuario a modificar.
     * @param roleName Nombre del nuevo rol (ej. "ADMIN").
     * @return El usuario actualizado o Optional.empty() si el usuario o rol no existen.
     */
    public Optional<Usuario> updateRole(Long userId, String roleName) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        if (usuarioOpt.isEmpty()) {
            return Optional.empty(); // Usuario no encontrado
        }

        Optional<Role> newRoleOpt = roleRepository.findByNombre(roleName);
        if (newRoleOpt.isEmpty()) {
            return Optional.empty(); // Rol no existe
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setRole(newRoleOpt.get());

        return Optional.of(usuarioRepository.save(usuario));
    }

    /**
     * Cambia el estado de la cuenta de un usuario (ACTIVO/BLOQUEADO).
     * @param userId ID del usuario.
     * @param estado El nuevo estado (EstadoUsuario.ACTIVO o EstadoUsuario.BLOQUEADO).
     * @return El usuario actualizado o Optional.empty() si el usuario no existe.
     */
    public Optional<Usuario> updateEstado(Long userId, EstadoUsuario estado) {
        return usuarioRepository.findById(userId).map(usuario -> {
            usuario.setEstado(estado);
            return usuarioRepository.save(usuario);
        });
    }

    /**
     * Elimina permanentemente a un usuario del sistema.
     * @param userId ID del usuario a eliminar.
     * @return true si el usuario fue encontrado y eliminado.
     */
    public boolean deleteUser(Long userId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        if (usuarioOpt.isPresent()) {
            usuarioRepository.delete(usuarioOpt.get());
            return true;
        }
        return false;
    }
}