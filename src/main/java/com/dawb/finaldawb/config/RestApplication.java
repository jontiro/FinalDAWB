package com.dawb.finaldawb.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

// Define la URL base para todos los recursos REST.
// Si el WAR se despliega como /FinalDAWB, la ruta será: /FinalDAWB/api/...
@ApplicationPath("/api")
public class RestApplication extends Application {
    // Nota: En Jakarta EE moderno (usando CDI), no es necesario listar explícitamente
    // los Resources (Controladores). CDI y JAX-RS los descubren automáticamente.
    // Esta clase solo define la ruta base.
}