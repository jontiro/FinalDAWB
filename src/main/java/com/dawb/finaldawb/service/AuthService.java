package com.dawb.finaldawb.service;

import com.dawb.finaldawb.domain.EstadoUsuario;
import com.dawb.finaldawb.domain.Role;
import com.dawb.finaldawb.domain.Usuario;
import com.dawb.finaldawb.repository.RoleRepository;
import com.dawb.finaldawb.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt; // <-- NUEVO IMPORT para la funcionalidad real de hasheo

import java.time.Instant;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class AuthService {

    @Inject
    private UsuarioRepository usuarioRepository;

    @Inject
    private RoleRepository roleRepository;

    // Constante para el rol por defecto
    private static final String DEFAULT_ROLE_NAME = "USER";

    /**
     * Registra un nuevo usuario en el sistema.
     * @param username Nombre de usuario.
     * @param email Correo electrónico.
     * @param password Contraseña sin hashear.
     * @return El usuario registrado o Optional.empty() si el usuario o email ya existen.
     */
    public Optional<Usuario> registrarUsuario(String username, String email, String password) {
        // 1. Verificar si el username o email ya existen
        if (usuarioRepository.findByUsername(username).isPresent() ||
                usuarioRepository.findByEmail(email).isPresent()) {
            return Optional.empty();
        }

        // 2. Buscar el rol por defecto (USER)
        Optional<Role> defaultRole = roleRepository.findByNombre(DEFAULT_ROLE_NAME);
        if (defaultRole.isEmpty()) {
            throw new IllegalStateException("El rol por defecto '" + DEFAULT_ROLE_NAME + "' no existe en la base de datos.");
        }

        // 3. Crear y configurar el nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setEmail(email);

        // 🚨 HASHEO REAL: Usamos BCrypt para generar el hash
        String hashedPassword = hashPassword(password);
        nuevoUsuario.setPasswordHash(hashedPassword);

        // Asignar el rol y estado inicial
        nuevoUsuario.setRole(defaultRole.get());
        nuevoUsuario.setEstado(EstadoUsuario.ACTIVO);

        // 4. Guardar en la BD
        return Optional.of(usuarioRepository.save(nuevoUsuario));
    }

    /**
     * Verifica las credenciales para el inicio de sesión.
     * @param username Nombre de usuario o email.
     * @param password Contraseña sin hashear.
     * @return El usuario si la autenticación es exitosa, o Optional.empty().
     */
    public Optional<Usuario> autenticar(String username, String password) {
        // 1. Buscar usuario por username o email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            usuarioOpt = usuarioRepository.findByEmail(username);
        }

        if (usuarioOpt.isEmpty()) {
            return Optional.empty(); // Usuario no encontrado
        }

        Usuario usuario = usuarioOpt.get();

        // 2. Verificar el estado
        if (!usuario.esActivo()) {
            return Optional.empty(); // Usuario bloqueado o inactivo
        }

        // 3. Comparar contraseñas hasheadas (USANDO BCrypt - REAL)
        if (verifyPassword(password, usuario.getPasswordHash())) {
            // Éxito: Actualizar la última conexión y devolver el usuario
            usuario.setUltimaConexion(Instant.now());
            usuarioRepository.save(usuario);
            return Optional.of(usuario);
        } else {
            return Optional.empty(); // Contraseña incorrecta
        }
    }

    // --- MÉTODOS DE BCrypt REALES ---

    /**
     * Genera un hash Bcrypt seguro para la contraseña dada.
     * @param rawPassword Contraseña en texto plano.
     * @return Hash seguro de la contraseña.
     */
    private String hashPassword(String rawPassword) {
        // Genera una "sal" (salt) aleatoria y luego hashea la contraseña
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * Verifica una contraseña en texto plano contra un hash existente.
     * @param rawPassword Contraseña en texto plano.
     * @param hashedPassword Hash almacenado en la BD.
     * @return true si la contraseña es correcta.
     */
    private boolean verifyPassword(String rawPassword, String hashedPassword) {
        // BCrypt maneja la lógica de extracción de la sal y el hasheo/comparación internamente.
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}