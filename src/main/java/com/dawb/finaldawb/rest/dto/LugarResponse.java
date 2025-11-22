package com.dawb.finaldawb.rest.dto;

import com.dawb.finaldawb.domain.Lugar;
import java.time.Instant;

// DTO para la respuesta de lugares (exponiendo solo campos necesarios)
public class LugarResponse {
    public Long id;
    public String nombre;
    public String direccion;
    public String ciudad;
    public String pais;
    public Long autorId;
    public String autorUsername;
    public Instant fechaCreacion;
    public Instant fechaActualizacion;

    public LugarResponse() {}

    /**
     * Mapea la entidad Lugar a un DTO de respuesta.
     * @param lugar Entidad Lugar.
     * @return DTO con datos seguros.
     */
    public static LugarResponse fromEntity(Lugar lugar) {
        LugarResponse response = new LugarResponse();
        response.id = lugar.getId();
        response.nombre = lugar.getNombre();
        response.direccion = lugar.getDireccion();
        response.ciudad = lugar.getCiudad();
        response.pais = lugar.getPais();
        response.fechaCreacion = lugar.getFechaCreacion();
        response.fechaActualizacion = lugar.getFechaActualizacion();

        // Mapeo del Autor
        if (lugar.getAutor() != null) {
            response.autorId = lugar.getAutor().getId();
            response.autorUsername = lugar.getAutor().getUsername();
        }

        return response;
    }
}