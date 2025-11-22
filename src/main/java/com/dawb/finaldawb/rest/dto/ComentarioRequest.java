package com.dawb.finaldawb.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO para crear o actualizar un comentario
public class ComentarioRequest {

    @NotBlank
    @Size(min = 5, max = 500)
    private String texto;

    @NotNull
    @Min(1)
    private Long recetaId;

    @NotNull
    @Min(1)
    // El ID del usuario que comenta (idealmente se obtendría del contexto de seguridad)
    private Long usuarioId;

    // Constructores, Getters y Setters
    public ComentarioRequest() {}

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public Long getRecetaId() { return recetaId; }
    public void setRecetaId(Long recetaId) { this.recetaId = recetaId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}