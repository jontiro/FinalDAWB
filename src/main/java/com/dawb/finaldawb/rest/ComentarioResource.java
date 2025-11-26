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
     * GET /comentarios : Obtiene todos los comentarios.
     * @return Lista de todos los comentarios.
     */
    @GET
    public List<ComentarioResponse> findAll() {
        return comentarioService.findAll()
                .stream()
                .map(ComentarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * POST /comentarios : Crea un nuevo comentario.
     * @param request Datos del comentario (texto, recetaId o lugarId, usuarioId).
     * @return 201 CREATED o 400 BAD REQUEST.
     */
    @POST
    public Response createComentario(@Valid ComentarioRequest request) {
        try {
            Comentario savedComentario;

            // Verificar si es un comentario de receta o de lugar
            if (request.getRecetaId() != null) {
                // Comentario de receta
                savedComentario = comentarioService.createComentarioReceta(
                        request.getTexto(),
                        request.getUsuarioId(),
                        request.getRecetaId()
                );
            } else if (request.getLugarId() != null) {
                // Comentario de lugar
                savedComentario = comentarioService.createComentarioLugar(
                        request.getTexto(),
                        request.getUsuarioId(),
                        request.getLugarId()
                );
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Debe proporcionar recetaId o lugarId\"}")
                        .build();
            }

            return Response.status(Response.Status.CREATED)
                    .entity(ComentarioResponse.fromEntity(savedComentario))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
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
     * @param recetaId ID de la receta.
     * @return Lista de comentarios.
     */
    @GET
    @Path("/receta/{recetaId}")
    public List<ComentarioResponse> findAllByRecetaId(@PathParam("recetaId") Long recetaId) {
        return comentarioService.findByRecetaId(recetaId)
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