package com.dawb.finaldawb.rest;

import com.dawb.finaldawb.domain.Comentario;
import com.dawb.finaldawb.rest.dto.ComentarioRequest;
import com.dawb.finaldawb.rest.dto.ComentarioResponse;
import com.dawb.finaldawb.service.ComentarioService;
import com.dawb.finaldawb.service.UsuarioService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/comentarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ComentarioResource {

    private final ComentarioService comentarioService;
    private final UsuarioService usuarioService;

    @Inject
    public ComentarioResource(ComentarioService comentarioService, UsuarioService usuarioService) {
        this.comentarioService = comentarioService;
        this.usuarioService = usuarioService;
    }

    // --- Puntos de Acceso (Endpoints) ---

    /**
     * POST /comentarios : Crea un nuevo comentario.
     * * NOTA: Asume que RecetaId en el DTO es el ID del Objeto genérico a comentar.
     * * @param request Datos del comentario (texto, idEntidad, tipoEntidad, usuarioId).
     * @return 201 CREATED o 404 NOT FOUND.
     */
    @POST
    public Response createComentario(@Valid ComentarioRequest request) {

        // 1. Validar existencia de Autor
        if (usuarioService.findById(request.getUsuarioId()).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Usuario no encontrado.")
                    .build();
        }

        Comentario nuevoComentario = new Comentario();
        nuevoComentario.setContenido(request.getTexto());

        // CORRECCIÓN: Se pasa el RecetaId del DTO como el ID del Objeto a comentar.
        Comentario savedComentario = comentarioService.createComentario(
                nuevoComentario,
                request.getUsuarioId(),
                request.getRecetaId(), // <--- ID del Objeto (Receta)
                "Receta" // Tipo de objeto fijo (simplificación)
        ).orElse(null);

        if (savedComentario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Error al crear comentario: la receta o el tipo Objeto no existe.")
                    .build();
        }

        // 4. Éxito: Devolver 201 CREATED
        return Response.status(Response.Status.CREATED)
                .entity(ComentarioResponse.fromEntity(savedComentario))
                .build();
    }

    /**
     * GET /comentarios/{id} : Obtiene un comentario por ID.
     * @param id ID del comentario.
     * @return 200 OK o 404 NOT FOUND.
     */
    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        return comentarioService.findById(id)
                .map(ComentarioResponse::fromEntity)
                .map(comentarioResponse -> Response.ok(comentarioResponse).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * GET /comentarios/receta/{recetaId} : Obtiene todos los comentarios de una receta.
     * * @param recetaId ID de la receta (objeto).
     * @return Lista de comentarios.
     */
    @GET
    @Path("/receta/{recetaId}")
    public List<ComentarioResponse> findAllByRecetaId(@PathParam("recetaId") Long recetaId) {
        // En el servicio, findAllByObjetoId asume que el recetaId pasado es el objetoId.
        return comentarioService.findAllByObjetoId(recetaId)
                .stream()
                .map(ComentarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * DELETE /comentarios/{id} : Elimina un comentario.
     * @param id ID del comentario.
     * @return 204 NO CONTENT o 404 NOT FOUND.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteComentario(@PathParam("id") Long id) {
        boolean deleted = comentarioService.deleteComentario(id);

        if (deleted) {
            return Response.noContent().build(); // 204 NO CONTENT
        } else {
            return Response.status(Response.Status.NOT_FOUND).build(); // 404
        }
    }
}