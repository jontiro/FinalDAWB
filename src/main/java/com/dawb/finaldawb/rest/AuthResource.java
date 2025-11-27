package com.dawb.finaldawb.rest;

import com.dawb.finaldawb.domain.Usuario;
import com.dawb.finaldawb.rest.dto.LoginRequest;
import com.dawb.finaldawb.rest.dto.RegistroRequest;
import com.dawb.finaldawb.security.CsrfTokenService;
import com.dawb.finaldawb.service.AuthService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final AuthService authService;
    private final CsrfTokenService csrfTokenService;

    @Inject
    public AuthResource(AuthService authService, CsrfTokenService csrfTokenService){
        this.authService = authService;
        this.csrfTokenService = csrfTokenService;
    }

    /**
     * Endpoint público para obtener un token CSRF después del login.
     * Este endpoint NO requiere token CSRF (está en la lista de exentos).
     * @return Token CSRF válido por 24 horas.
     */
    @GET
    @Path("/csrf-token")
    public Response getCsrfToken() {
        String token = csrfTokenService.generateToken();
        return Response.ok(new CsrfTokenResponse(token)).build();
    }

    /**
     * Endpoint para el registro de nuevos usuarios.
     * @param request Datos de registro (username, email, password).
     * @return 201 CREATED con el usuario registrado o 400 BAD REQUEST si ya existe.
     */
    @POST
    @Path("/register")
    public Response register(@Valid RegistroRequest request) {
        // 1. Llamar al servicio para realizar el registro
        Usuario nuevoUsuario = authService.registrarUsuario(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        ).orElse(null);

        if (nuevoUsuario == null) {
            // 2. Si el servicio devuelve Optional.empty(), el usuario/email ya existe
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("El nombre de usuario o correo electrónico ya está registrado."))
                    .build();
        }

        // 3. Éxito: Devolver 201 CREATED y los datos básicos del usuario
        return Response.status(Response.Status.CREATED)
                .entity(mapToDto(nuevoUsuario))
                .build();
    }

    /**
     * Endpoint para el inicio de sesión.
     * @param request Datos de login (username/email y password).
     * @return 200 OK con el usuario (y token simulado) o 401 UNAUTHORIZED.
     */
    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        // 1. Llamar al servicio para autenticar
        Usuario usuario = authService.autenticar(
                request.getUsernameOrEmail(),
                request.getPassword()
        ).orElse(null);

        if (usuario == null) {
            // 2. Fallo de autenticación o usuario bloqueado
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Credenciales inválidas o usuario inactivo."))
                    .build();
        }

        // 3. Éxito: Devolver 200 OK y el usuario. (Simulación de generación de token)
        return Response.ok(mapToDto(usuario))
                .build();
    }

    /**
     * Endpoint público para obtener el conteo de usuarios registrados.
     * @return Conteo de usuarios activos.
     */
    @GET
    @Path("/count")
    public Response getUserCount() {
        try {
            long count = authService.contarUsuariosActivos();
            return Response.ok(new CountResponse(count)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error al obtener el conteo de usuarios"))
                    .build();
        }
    }

    // --- Mapeo de Seguridad (CRÍTICO) ---

    // Mapea la entidad Usuario a un DTO de respuesta para EVITAR exponer el passwordHash.
    // Esto es una capa de seguridad vital.
    private UserResponse mapToDto(Usuario usuario) {
        // En una aplicación real, solo mapearías los campos seguros (ID, username, email, role)
        // y generarías un token JWT. Aquí devolvemos una estructura simple.
        return new UserResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRole().getNombre() // Asumiendo que el Role está cargado
        );
    }

    // DTO de respuesta interna (debería estar en com.dawb.finaldawb.rest.dto)
    // Se define aquí por simplicidad de archivo
    public static class UserResponse {
        public Long id;
        public String username;
        public String email;
        public String role;

        public UserResponse(Long id, String username, String email, String role) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.role = role;
        }
    }

    // DTO para respuestas de error
    public static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }

    // DTO para respuesta de conteo
    public static class CountResponse {
        public long count;

        public CountResponse(long count) {
            this.count = count;
        }
    }

    // DTO para respuesta de token CSRF
    public static class CsrfTokenResponse {
        public String token;

        public CsrfTokenResponse(String token) {
            this.token = token;
        }
    }
}