package com.dawb.finaldawb.config;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        // Descubrimiento por paquete
        packages("com.dawb.finaldawb.rest");
        // Registros adicionales (solo aquí)
        // register(com.dawb.finaldawb.auth.AuthFilter.class);
        // register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }
}