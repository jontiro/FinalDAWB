package com.dawb.finaldawb.rest.dto;

// DTO para mostrar los pasos de una receta
public class PasoResponse {
    public Long id;
    public Short orden;
    public String descripcion;

    public PasoResponse() {}
    public PasoResponse(Long id, Short orden, String descripcion) {
        this.id = id;
        this.orden = orden;
        this.descripcion = descripcion;
    }
}