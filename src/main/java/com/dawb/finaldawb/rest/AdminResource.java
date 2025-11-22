package com.dawb.finaldawb.rest;

import com.dawb.finaldawb.domain.Comentario;
import com.dawb.finaldawb.domain.Tipo;
import com.dawb.finaldawb.domain.Objeto;
import com.dawb.finaldawb.rest.dto.ComentarioResponse;
import com.dawb.finaldawb.service.ComentarioService;
import com.dawb.finaldawb.service.TipoService;
import com.dawb.finaldawb.service.ObjetoService;
import com.dawb.finaldawb.service.UsuarioService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

// Este recurso asume que el usuario tiene el rol 'ADMIN' o 'MODERADOR'
// La verificación de roles debe hacerse con filtros de seguridad antes de llegar aquí (no implementado en JAX-RS base)
@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminResource {

    private final ComentarioService comentarioService;
    private final TipoService tipoService;
    private final ObjetoService objetoService;
    private final UsuarioService usuarioService;

    @Inject
    public AdminResource(ComentarioService comentarioService, TipoService tipoService, ObjetoService objetoService, UsuarioService usuarioService) {
        this.comentarioService = comentarioService;
        this.tipoService = tipoService;
        this.objetoService = objetoService;
        this.usuarioService = usuarioService;
    }

    // --- 1. MODERACIÓN DE COMENTARIOS ---

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
}