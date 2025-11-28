package com.dawb.finaldawb.rest;

import com.dawb.finaldawb.domain.Lugar;
import com.dawb.finaldawb.rest.dto.LugarRequest;
import com.dawb.finaldawb.rest.dto.LugarResponse;
import com.dawb.finaldawb.service.LugarService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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
     * @param request Objeto LugarRequest con datos del lugar y autorId.
     * @return 201 Created con la URI del nuevo recurso.
     */
    @POST
    public Response createLugar(@Valid LugarRequest request) {
        try {
            // Crear entidad Lugar desde el DTO
            Lugar lugar = new Lugar();
            lugar.setNombre(request.getNombre());
            lugar.setDireccion(request.getDireccion());
            lugar.setCiudad(request.getCiudad());
            lugar.setPais(request.getPais());

            // Crear el lugar con el autorId del request
            Lugar nuevoLugar = lugarService.createLugar(lugar, request.getAutorId())
                    .orElse(null);

            if (nuevoLugar == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Usuario no encontrado con ID: " + request.getAutorId() + "\"}")
                        .build();
            }

            // Retorna 201 Created con la ubicación del recurso
            return Response.created(URI.create("/lugares/" + nuevoLugar.getId()))
                    .entity(LugarResponse.fromEntity(nuevoLugar))
                    .build();

        } catch (Exception e) {
            // Log del error para debugging
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno: " + e.getMessage() + "\"}")
                    .build();
        }
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
     * @param userId ID del usuario que intenta actualizar (desde header X-User-Id).
     * @return 200 OK con el lugar actualizado o 404 NOT FOUND/403 FORBIDDEN.
     */
    @PUT
    @Path("/{id}")
    public Response updateLugar(@PathParam("id") Long id, Lugar lugar, @HeaderParam("X-User-Id") Long userId) {
        // Si no se proporciona el header, retornar error
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Usuario no autenticado. Debe proporcionar el header X-User-Id.\"}")
                    .build();
        }

        lugar.setId(id); // Asegura que el ID del objeto coincida con el path

        // Llamar al servicio con el userId del header
        return lugarService.updateLugar(lugar, userId)
                .map(LugarResponse::fromEntity)
                .map(lugarResponse -> Response.ok(lugarResponse).build())
                .orElse(Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"Lugar no encontrado o no autorizado para editar.\"}")
                        .build());
    }

    /**
     * DELETE /lugares/{id} : Elimina un lugar por su ID.
     * @param id ID del lugar a eliminar.
     * @param userId ID del usuario que intenta eliminar (desde header X-User-Id).
     * @return 204 No Content si se eliminó, 404 Not Found si no existe o no autorizado.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteLugar(@PathParam("id") Long id, @HeaderParam("X-User-Id") Long userId) {
        // Si no se proporciona el header, intentar con un valor por defecto
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Usuario no autenticado. Debe proporcionar el header X-User-Id.\"}")
                    .build();
        }

        // Llamar al servicio para eliminar
        if (lugarService.deleteLugar(id, userId)) {
            // Retorna 204 No Content al eliminar exitosamente
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Lugar no encontrado o no autorizado para eliminar.\"}")
                    .build();
        }
    }
}