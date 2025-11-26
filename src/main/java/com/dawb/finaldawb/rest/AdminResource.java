package com.dawb.finaldawb.rest;

import com.dawb.finaldawb.domain.*;
import com.dawb.finaldawb.rest.dto.ComentarioResponse;
import com.dawb.finaldawb.rest.dto.UsuarioResponse;
import com.dawb.finaldawb.rest.dto.UsuarioRequest;
import com.dawb.finaldawb.rest.dto.RecetaResponse;
import com.dawb.finaldawb.rest.dto.LugarResponse;
import com.dawb.finaldawb.service.*;
import com.dawb.finaldawb.repository.RoleRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.stream.Collectors;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminResource {

    private final ComentarioService comentarioService;
    private final TipoService tipoService;
    private final ObjetoService objetoService;
    private final UsuarioService usuarioService;
    private final RecetaService recetaService;
    private final LugarService lugarService;
    private final RoleRepository roleRepository;

    @Inject
    public AdminResource(ComentarioService comentarioService, TipoService tipoService,
                        ObjetoService objetoService, UsuarioService usuarioService,
                        RecetaService recetaService, LugarService lugarService,
                        RoleRepository roleRepository) {
        this.comentarioService = comentarioService;
        this.tipoService = tipoService;
        this.objetoService = objetoService;
        this.usuarioService = usuarioService;
        this.recetaService = recetaService;
        this.lugarService = lugarService;
        this.roleRepository = roleRepository;
    }

    // ==================== USUARIOS ====================

    @GET
    @Path("/usuarios")
    public List<UsuarioResponse> getAllUsuarios() {
        return usuarioService.findAll().stream()
                .map(UsuarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @POST
    @Path("/usuarios")
    public Response createUsuario(UsuarioRequest request) {
        try {
            Usuario usuario = new Usuario();
            usuario.setUsername(request.getUsername());
            usuario.setEmail(request.getEmail());
            usuario.setPasswordHash(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
            usuario.setEstado(EstadoUsuario.valueOf(request.getEstado()));

            Role role = roleRepository.findById(request.getRoleId()).orElse(null);
            if (role == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Rol no encontrado\"}")
                        .build();
            }
            usuario.setRole(role);

            Usuario saved = usuarioService.save(usuario);
            return Response.status(Response.Status.CREATED)
                    .entity(UsuarioResponse.fromEntity(saved))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PUT
    @Path("/usuarios/{id}")
    public Response updateUsuario(@PathParam("id") Long id, UsuarioRequest request) {
        Usuario usuario = usuarioService.findById(id).orElse(null);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.setPasswordHash(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }
        usuario.setEstado(EstadoUsuario.valueOf(request.getEstado()));

        Role role = roleRepository.findById(request.getRoleId()).orElse(null);
        if (role != null) {
            usuario.setRole(role);
        }

        Usuario updated = usuarioService.update(usuario);
        return Response.ok(UsuarioResponse.fromEntity(updated)).build();
    }

    @DELETE
    @Path("/usuarios/{id}")
    public Response deleteUsuario(@PathParam("id") Long id) {
        if (usuarioService.delete(id)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // ==================== COMENTARIOS ====================

    @GET
    @Path("/comentarios/todos")
    public List<ComentarioResponse> getAllComentarios() {
        // Este endpoint debería retornar TODOS los comentarios, no solo pendientes
        return usuarioService.findAll().stream()
                .flatMap(u -> comentarioService.findByAutorId(u.getId()).stream())
                .map(ComentarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * GET /admin/moderacion/pendientes : Obtiene comentarios no moderados.
     */
    @GET
    @Path("/moderacion/pendientes")
    public List<ComentarioResponse> getComentariosPendientes() {
        // Lógica de Negocio: Asumimos que solo los admin/mod pueden ver esto
        return comentarioService.findPendientesModeracion().stream()
                .map(ComentarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * POST /admin/moderacion/{id}/aprobar : Aprueba o rechaza un comentario.
     * @param id ID del comentario.
     * @param estado 'true' para aprobar, 'false' para rechazar/ocultar.
     * @return 200 OK o 404 NOT FOUND.
     */
    @POST
    @Path("/moderacion/{id}/estado")
    public Response updateModeracion(@PathParam("id") Long id, @QueryParam("moderado") boolean estado) {
        return comentarioService.updateModeracion(id, estado)
                .map(ComentarioResponse::fromEntity)
                .map(comentarioResponse -> Response.ok(comentarioResponse).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // --- 2. GESTIÓN DE CATÁLOGOS (TIPOS DE RECOMENDACION) ---

    /**
     * GET /admin/tipos : Obtiene todos los tipos de recomendación.
     */
    @GET
    @Path("/tipos")
    public List<Tipo> findAllTipos() {
        return tipoService.findAll();
    }

    /**
     * POST /admin/tipos/{descripcion} : Crea un nuevo tipo.
     */
    @POST
    @Path("/tipos/{descripcion}")
    public Response createTipo(@PathParam("descripcion") String descripcion) {
        return tipoService.createTipo(descripcion)
                .map(tipo -> Response.status(Response.Status.CREATED).entity(tipo).build())
                .orElse(Response.status(Response.Status.BAD_REQUEST)
                        .entity("Tipo ya existe o es inválido.").build());
    }

    /**
     * DELETE /admin/tipos/{id} : Elimina un tipo.
     */
    @DELETE
    @Path("/tipos/{id}")
    public Response deleteTipo(@PathParam("id") Long id) {
        // La restricción de la BD (FK) debe manejar si hay recomendaciones asociadas.
        if (tipoService.deleteTipo(id)) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // --- 3. GESTIÓN DE CATÁLOGOS (OBJETOS COMENTABLES) ---

    /**
     * GET /admin/objetos : Obtiene todos los objetos comentables (Receta, Lugar, etc.).
     */
    @GET
    @Path("/objetos")
    public List<Objeto> findAllObjetos() {
        return objetoService.findAll();
    }

    /**
     * POST /admin/objetos/{descripcion} : Crea un nuevo objeto comentable.
     * (Esto es útil si añades una nueva entidad, ej. 'Blog')
     */
    @POST
    @Path("/objetos/{descripcion}")
    public Response createObjeto(@PathParam("descripcion") String descripcion) {
        return objetoService.createObjeto(descripcion)
                .map(objeto -> Response.status(Response.Status.CREATED).entity(objeto).build())
                .orElse(Response.status(Response.Status.BAD_REQUEST)
                        .entity("Objeto ya existe o es inválido.").build());
    }

    /**
     * DELETE /admin/objetos/{id} : Elimina un objeto comentable.
     */
    @DELETE
    @Path("/objetos/{id}")
    public Response deleteObjeto(@PathParam("id") Long id) {
        if (objetoService.deleteObjeto(id)) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // --- 4. INICIALIZACIÓN DE DATOS DE PRUEBA ---

    /**
     * POST /admin/init-data : Inicializa datos de prueba en la base de datos.
     * Este endpoint es útil para desarrollo y testing.
     * ADVERTENCIA: Solo usar en desarrollo, no en producción.
     */
    @POST
    @Path("/init-data")
    public Response initializeTestData() {
        try {
            // 1. Crear roles si no existen
            if (roleRepository.findByNombre("USER").isEmpty()) {
                Role userRole = new Role();
                userRole.setNombre("USER");
                userRole.setDescripcion("Usuario estándar con permisos básicos");
                userRole.setActivo(true);
                roleRepository.save(userRole);
            }

            if (roleRepository.findByNombre("ADMIN").isEmpty()) {
                Role adminRole = new Role();
                adminRole.setNombre("ADMIN");
                adminRole.setDescripcion("Administrador con todos los permisos");
                adminRole.setActivo(true);
                roleRepository.save(adminRole);
            }

            // 2. Crear usuario de prueba si no existe
            if (usuarioService.findByUsername("testuser").isEmpty()) {
                Usuario testUser = new Usuario();
                testUser.setUsername("testuser");
                testUser.setEmail("test@example.com");
                // Password: "password123"
                testUser.setPasswordHash(BCrypt.hashpw("password123", BCrypt.gensalt()));
                testUser.setRole(roleRepository.findByNombre("USER").orElseThrow());
                testUser.setEstado(EstadoUsuario.ACTIVO);
                usuarioService.save(testUser);
            }

            return Response.ok()
                    .entity("{\"message\": \"Datos de prueba inicializados correctamente\"}")
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error al inicializar datos: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}