package com.dawb.finaldawb.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

// DTO para la creación/actualización de una receta
public class RecetaRequest {
    @NotBlank
    @Size(max = 100)
    private String titulo;

    @NotBlank
    private String descripcion;

    @NotNull
    @Min(1) // El tiempo de preparación debe ser al menos 1 minuto
    private Short tiempoPreparacion;

    // ID del usuario que crea la receta (se obtiene del contexto de seguridad, pero se pasa aquí por simplicidad)
    @NotNull
    private Long creadorId;

    // La privacidad es opcional en la petición, por defecto es false (pública)
    private Boolean privacidad;

    // Lista de pasos anidados
    @Valid
    private List<PasoRequest> pasos;

    // Lista de nombres de tags (opción 1 - para crear nuevos o buscar existentes)
    private List<String> tags;

    // Lista de IDs de tags (opción 2 - más eficiente, usar tags existentes)
    private List<Long> tagIds;

    // Constructores, Getters y Setters
    public RecetaRequest() {}

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Short getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    public void setTiempoPreparacion(Short tiempoPreparacion) {
        this.tiempoPreparacion = tiempoPreparacion;
    }

    public Long getCreadorId() {
        return creadorId;
    }

    public void setCreadorId(Long creadorId) {
        this.creadorId = creadorId;
    }

    public Boolean getPrivacidad() {
        return privacidad;
    }

    public void setPrivacidad(Boolean privacidad) {
        this.privacidad = privacidad;
    }

    public List<PasoRequest> getPasos() {
        return pasos;
    }

    public void setPasos(List<PasoRequest> pasos) {
        this.pasos = pasos;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
}