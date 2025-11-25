package com.dawb.finaldawb.rest.dto;

import com.dawb.finaldawb.domain.Comentario;
import java.time.Instant;

// DTO para la respuesta de comentarios
public class ComentarioResponse {
    public Long id;
    public String texto;
    public Instant fechaCreacion;
    public Long recetaId;
    public Long autorId;
    public String autorUsername;

    public ComentarioResponse() {}

    // Método de mapeo estático de la entidad al DTO
    public static ComentarioResponse fromEntity(Comentario comentario) {
        ComentarioResponse response = new ComentarioResponse();
        response.id = comentario.getId();
        response.texto = comentario.getContenido();
        response.fechaCreacion = comentario.getFechaCreacion();

        // Usar entidadId en lugar de objeto.id
        response.recetaId = comentario.getEntidadId();

        if (comentario.getAutor() != null) {
            response.autorId = comentario.getAutor().getId();
            response.autorUsername = comentario.getAutor().getUsername();
        }

        return response;
    }
}