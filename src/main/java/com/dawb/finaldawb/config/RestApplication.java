package com.dawb.finaldawb.config;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Configuración principal de la aplicación JAX-RS.
 *
 * Esta clase configura Jersey para:
 * 1. Escanear y registrar automáticamente todos los Resources (@Path)
 * 2. Registrar el puente CDI-HK2 para inyección de dependencias
 *
 * @ApplicationPath define la ruta base de todos los endpoints REST
 */
@ApplicationPath("/api")
public class RestApplication extends ResourceConfig {

    /**
     * Constructor que configura la aplicación Jersey.
     *
     * Se ejecuta al iniciar la aplicación y configura:
     * - El escaneo de packages para encontrar Resources
     * - El registro del CdiHk2Binder para integración CDI-HK2
     */
    public RestApplication() {
        // Escanear el package com.dawb.finaldawb.rest para encontrar
        // automáticamente todas las clases con @Path
        packages("com.dawb.finaldawb.rest");

        // Registrar el puente entre CDI (Weld) y HK2 (Jersey)
        // Esto permite que HK2 inyecte beans gestionados por CDI
        // en los Resources de Jersey
        register(new CdiHk2Binder());

        // Registrar la configuración de Jackson para soportar Java 8 Date/Time API
        // Esto permite serializar/deserializar Instant, LocalDateTime, etc.
        register(JacksonConfig.class);
    }
}

