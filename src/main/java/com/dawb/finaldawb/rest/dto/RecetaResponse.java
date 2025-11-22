package com.dawb.finaldawb.rest.dto;

import com.dawb.finaldawb.domain.Receta;
import com.dawb.finaldawb.domain.RecetaPaso;
import com.dawb.finaldawb.domain.RecetaTag;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

// DTO para la respuesta de recetas (exponiendo solo campos necesarios)
public class RecetaResponse {
    public Long id;
    public String titulo;
    public String descripcion;
    public Short tiempoPreparacion;
    public Long creadorId;
    public String creadorUsername;
    public Instant fechaCreacion;
    public Instant fechaActualizacion;
    public Boolean privacidad;
    public List<PasoResponse> pasos;
    public List<String> tags; // Solo los nombres de las etiquetas

    public RecetaResponse() {}

    // Método de mapeo estático de la entidad al DTO
    public static RecetaResponse fromEntity(Receta receta) {
        RecetaResponse response = new RecetaResponse();
        response.id = receta.getId();
        response.titulo = receta.getTitulo();
        response.descripcion = receta.getDescripcion();
        response.tiempoPreparacion = receta.getTiempoPreparacion();
        response.creadorId = receta.getCreador().getId();
        response.creadorUsername = receta.getCreador().getUsername();
        response.fechaCreacion = receta.getFechaCreacion();
        response.fechaActualizacion = receta.getFechaActualizacion();
        response.privacidad = receta.isPrivacidad();

        // Mapear Pasos
        if (receta.getPasos() != null) {
            response.pasos = receta.getPasos().stream()
                    .map(p -> new PasoResponse(p.getId(), p.getOrden(), p.getDescripcion()))
                    .collect(Collectors.toList());
        }

        // Mapear Tags (extrayendo solo el nombre)
        if (receta.getRecetaTags() != null) {
            response.tags = receta.getRecetaTags().stream()
                    .map(rt -> rt.getTag().getNombre())
                    .collect(Collectors.toList());
        }

        return response;
    }
}