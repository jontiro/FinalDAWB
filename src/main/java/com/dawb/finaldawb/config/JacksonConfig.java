package com.dawb.finaldawb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

/**
 * Configuración de Jackson para JAX-RS.
 * 
 * Esta clase configura el ObjectMapper para:
 * 1. Soportar tipos de Java 8 Date/Time API (Instant, LocalDateTime, etc.)
 * 2. Serializar fechas como timestamps ISO-8601 en lugar de arrays
 */
@Provider
public class JacksonConfig implements ContextResolver<ObjectMapper> {

    private final ObjectMapper objectMapper;

    public JacksonConfig() {
        objectMapper = new ObjectMapper();
        
        // Registrar el módulo JavaTimeModule para soportar java.time.*
        // Esto permite serializar/deserializar Instant, LocalDateTime, etc.
        objectMapper.registerModule(new JavaTimeModule());
        
        // Configurar para serializar fechas como strings ISO-8601
        // en lugar de timestamps numéricos
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Configuración opcional: pretty printing en desarrollo
        // objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
}

