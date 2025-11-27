package com.dawb.finaldawb.rest;

import com.dawb.finaldawb.domain.Role;
import com.dawb.finaldawb.repository.RoleRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
public class RoleResource {

    private final RoleRepository roleRepository;

    @Inject
    public RoleResource(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * GET /roles : Obtiene todos los roles disponibles.
     * @return Lista de roles.
     */
    @GET
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}

