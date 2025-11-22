package com.dawb.finaldawb.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

// DTO para recibir los pasos de una receta al crearla
public class PasoRequest {
    @NotNull
    @PositiveOrZero
    private Short orden;

    @NotBlank
    private String descripcion;

    // Constructores, Getters y Setters
    public PasoRequest() {}

    public Short getOrden() { return orden; }
    public void setOrden(Short orden) { this.orden = orden; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}