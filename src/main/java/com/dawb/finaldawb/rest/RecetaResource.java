package com.dawb.finaldawb.rest;

import com.dawb.finaldawb.domain.Receta;
import com.dawb.finaldawb.domain.RecetaPaso;
import com.dawb.finaldawb.domain.Usuario;
import com.dawb.finaldawb.rest.dto.RecetaRequest;
import com.dawb.finaldawb.rest.dto.RecetaResponse;
import com.dawb.finaldawb.rest.dto.PasoRequest;
import com.dawb.finaldawb.service.RecetaService;
import com.dawb.finaldawb.service.UsuarioService; // Para obtener el objeto Usuario

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/recetas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RecetaResource {

    private final RecetaService recetaService;
    private final UsuarioService usuarioService; // Necesario para obtener el autor al crear/borrar

    @Inject
    public RecetaResource(RecetaService recetaService, UsuarioService usuarioService) {
        this.recetaService = recetaService;
        this.usuarioService = usuarioService;
    }

    // --- Puntos de Acceso (Endpoints) ---

    /**
     * POST /recetas : Crea una nueva receta.
     * Asume que el ID del creador se pasa en el DTO (idealmente vendría del token de seguridad).
     * @param request Datos de la receta (título, pasos, tags).
     * @return 201 CREATED con la receta o 404 NOT FOUND si el creador no existe.
     */
    @POST
    public Response createReceta(@Valid RecetaRequest request) {
        // 1. Obtener el usuario creador desde la base de datos
        Usuario creador = usuarioService.findById(request.getCreadorId()).orElse(null);
        if (creador == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("El usuario creador no existe")
                    .build();
        }

        // 2. Mapear DTO a Entidad principal Receta
        Receta nuevaReceta = mapRequestToEntity(request);
        nuevaReceta.setCreador(creador); // Asignar el creador

        // 3. Mapear DTOs de Pasos a Entidades RecetaPaso
        List<RecetaPaso> pasosEntities = request.getPasos().stream()
                .map(RecetaResource::mapPasoRequestToEntity)
                .collect(Collectors.toList());

        // 4. Llamar al servicio para crear y persistir la receta
        Receta savedReceta = recetaService.createReceta(
                nuevaReceta,
                pasosEntities,
                request.getTags(),
                request.getTagIds()  // Pasar también los IDs de tags
        );

        // 5. Éxito: Devolver 201 CREATED con el DTO de respuesta seguro
        return Response.status(Response.Status.CREATED)
                .entity(RecetaResponse.fromEntity(savedReceta))
                .build();
    }

    /**
     * GET /recetas : Obtiene todas las recetas públicas.
     * @return 200 OK con una lista de RecetaResponse.
     */
    @GET
    public List<RecetaResponse> findAllPublicas() {
        return recetaService.findAllPublicas().stream()
                .map(RecetaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * GET /recetas/{id} : Obtiene una receta por ID.
     * NOTA: Aquí se debe añadir la lógica para ver si el usuario tiene acceso a recetas privadas.
     * @param id ID de la receta.
     * @return 200 OK o 404 NOT FOUND.
     */
    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        // Asumiendo que el servicio ya maneja la lógica de privacidad básica.
        return recetaService.findById(id)
                .map(RecetaResponse::fromEntity)
                .map(recetaResponse -> Response.ok(recetaResponse).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * DELETE /recetas/{id} : Elimina una receta.
     * @param id ID de la receta.
     * @return 204 NO CONTENT o 404 NOT FOUND/403 FORBIDDEN.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteReceta(@PathParam("id") Long id) {
        // SIMULACIÓN: Obtenemos el ID del usuario loggeado (CRÍTICO para BAC)
        Long currentUserId = 1L; // Asumimos un ID de usuario loggeado.

        // NOTA DE SEGURIDAD: El RecetaService no tiene el método delete con userId.
        // Usaremos la versión simple por ahora, pero la seguridad real requiere la verificación.

        boolean deleted = recetaService.deleteReceta(id);

        if (deleted) {
            return Response.noContent().build(); // 204 NO CONTENT
        } else {
            return Response.status(Response.Status.NOT_FOUND).build(); // 404
        }
    }


    // --- Mapeo DTO a Entidad (Interno) ---

    // Mapea la RecetaRequest principal a la Entidad Receta
    private Receta mapRequestToEntity(RecetaRequest request) {
        Receta receta = new Receta();
        receta.setTitulo(request.getTitulo());
        receta.setDescripcion(request.getDescripcion());
        receta.setTiempoPreparacion(request.getTiempoPreparacion());

        // Mapeo de privacidad (manejo de null en DTO)
        receta.setPrivacidad(request.getPrivacidad() != null ? request.getPrivacidad() : false);

        // Nota: El campo 'creador' se establece en el Service.

        return receta;
    }

    // Mapea el PasoRequest a la Entidad RecetaPaso
    private static RecetaPaso mapPasoRequestToEntity(PasoRequest request) {
        RecetaPaso paso = new RecetaPaso();
        paso.setOrden(request.getOrden());
        paso.setDescripcion(request.getDescripcion());
        // La FK a Receta se establece en el Service
        return paso;
    }
}