package com.dawb.finaldawb.rest.dto;

import com.dawb.finaldawb.domain.Usuario;
import java.time.Instant;

public class UsuarioResponse {
    public Long id;
    public String username;
    public String email;
    public String estado;
    public Long roleId;
    public String roleNombre;
    public Instant fechaRegistro;
    public Instant ultimaConexion;
    
    public UsuarioResponse() {}
    
    public static UsuarioResponse fromEntity(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.id = usuario.getId();
        response.username = usuario.getUsername();
        response.email = usuario.getEmail();
        response.estado = usuario.getEstado() != null ? usuario.getEstado().name() : "ACTIVO";
        response.fechaRegistro = usuario.getFechaRegistro();
        response.ultimaConexion = usuario.getUltimaConexion();
        
        if (usuario.getRole() != null) {
            response.roleId = usuario.getRole().getId();
            response.roleNombre = usuario.getRole().getNombre();
        }
        
        return response;
    }
}

