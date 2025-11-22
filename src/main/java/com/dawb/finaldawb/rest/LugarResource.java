package com.dawb.finaldawb.rest;

import com.dawb.finaldawb.domain.Lugar;
import com.dawb.finaldawb.rest.dto.LugarResponse;
import com.dawb.finaldawb.service.LugarService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

// Endpoint principal para la gestión de Lugares
@Path("/lugares")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LugarResource {

    private final LugarService lugarService;

    @Inject
    public LugarResource(LugarService lugarService) {
        this.lugarService = lugarService;
    }

    /**
     * POST /lugares : Crea un nuevo lugar.
     * @param lugar Objeto Lugar (entidad de dominio, idealmente un DTO).
     * @return 201 Created con la URI del nuevo recurso.
     */
    @POST
    public Response createLugar(Lugar lugar) {
        // SIMULACIÓN DE SEGURIDAD: Asignar un autor_id conocido.
        Long autorId = 1L; // Asume que el usuario 1 está creando esto.

        // CORRECCIÓN 1: Usar createLugar y pasar el autorId
        Lugar nuevoLugar = lugarService.createLugar(lugar, autorId)
                .orElse(null);

        if (nuevoLugar == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error al crear lugar: el autor no existe.")
                    .build();
        }

        // Retorna 201 Created con la ubicación del recurso
        return Response.created(URI.create("/lugares/" + nuevoLugar.getId()))
                .entity(LugarResponse.fromEntity(nuevoLugar))
                .build();
    }

    /**
     * GET /lugares : Obtiene todos los lugares.
     */
    @GET
    public List<LugarResponse> findAllLugares() {
        // CORRECCIÓN 2: Usar findAll()
        return lugarService.findAll().stream()
                .map(LugarResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * GET /lugares/{id} : Obtiene un lugar por su ID.
     */
    @GET
    @Path("/{id}")
    public Response getLugarById(@PathParam("id") Long id) {
        // CORRECCIÓN 3: Usar findById(id)
        return lugarService.findById(id)
                .map(LugarResponse::fromEntity)
                .map(lugarResponse -> Response.ok(lugarResponse).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * PUT /lugares/{id} : Actualiza un lugar existente.
     * @param id ID del lugar a actualizar.
     * @param lugar Objeto Lugar con los datos a actualizar.
     * @return 200 OK con el lugar actualizado o 404 NOT FOUND/403 FORBIDDEN.
     */
    @PUT
    @Path("/{id}")
    public Response updateLugar(@PathParam("id") Long id, Lugar lugar) {
        // SIMULACIÓN DE SEGURIDAD: ID del usuario loggeado
        Long currentUserId = 1L;

        lugar.setId(id); // Asegura que el ID del objeto coincida con el path

        // CORRECCIÓN 4: Pasar el currentUserId para la verificación de permisos
        return lugarService.updateLugar(lugar, currentUserId)
                .map(LugarResponse::fromEntity)
                .map(lugarResponse -> Response.ok(lugarResponse).build())
                .orElse(Response.status(Response.Status.FORBIDDEN)
                        .entity("Lugar no encontrado o no autorizado para editar.").build());
    }

    /**
     * DELETE /lugares/{id} : Elimina un lugar por su ID.
     * @param id ID del lugar a eliminar.
     * @return 204 No Content si se eliminó, 404 Not Found si no existe.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteLugar(@PathParam("id") Long id) {
        // SIMULACIÓN DE SEGURIDAD: ID del usuario loggeado
        Long currentUserId = 1L;

        // CORRECCIÓN 5: Pasar el currentUserId para la verificación de permisos
        if (lugarService.deleteLugar(id, currentUserId)) {
            // Retorna 204 No Content al eliminar exitosamente
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Lugar no encontrado o no autorizado para eliminar.")
                    .build();
        }
    }
}