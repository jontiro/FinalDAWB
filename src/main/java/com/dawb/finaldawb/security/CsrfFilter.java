package com.dawb.finaldawb.security;

import com.dawb.finaldawb.security.CsrfTokenService;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtro JAX-RS para validar tokens CSRF en peticiones de escritura.
 * Se aplica automáticamente a todos los endpoints POST, PUT, DELETE.
 *
 * RUTAS EXENTAS: login, registro, obtener token CSRF, estadísticas públicas
 */
@Provider
public class CsrfFilter implements ContainerRequestFilter {

    @Inject
    private CsrfTokenService csrfTokenService;

    // Métodos HTTP que requieren validación CSRF
    private static final List<String> CSRF_PROTECTED_METHODS = Arrays.asList("POST", "PUT", "DELETE", "PATCH");

    // Rutas que NO requieren CSRF (login, registro, obtener token, estadísticas)
    private static final List<String> CSRF_EXEMPT_PATHS = Arrays.asList(
        "auth/login",           // Login NO requiere CSRF
        "auth/register",        // Registro NO requiere CSRF
        "auth/csrf-token",      // Obtener token NO requiere CSRF
        "auth/count"            // Estadísticas públicas
    );

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();

        // Solo validar en métodos de escritura
        if (!CSRF_PROTECTED_METHODS.contains(method)) {
            return;
        }

        // Omitir rutas exentas
        for (String exemptPath : CSRF_EXEMPT_PATHS) {
            if (path.contains(exemptPath)) {
                return;
            }
        }

        // Obtener token del header
        String csrfToken = requestContext.getHeaderString("X-CSRF-Token");

        // Validar token
        if (csrfToken == null || !csrfTokenService.isValid(csrfToken)) {
            requestContext.abortWith(
                Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"error\": \"Token CSRF inválido o expirado. Por favor, recarga la página.\"}")
                    .build()
            );
        }
    }
}

