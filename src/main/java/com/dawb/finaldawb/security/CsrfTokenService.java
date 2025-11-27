package com.dawb.finaldawb.security;

import jakarta.enterprise.context.ApplicationScoped;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Servicio para generar y validar tokens CSRF.
 * Protege contra ataques Cross-Site Request Forgery.
 */
@ApplicationScoped
public class CsrfTokenService {

    private static final int TOKEN_LENGTH = 32;
    private static final long TOKEN_VALIDITY_HOURS = 24;
    
    private final SecureRandom secureRandom = new SecureRandom();
    
    // Almacén en memoria de tokens (en producción usar Redis o similar)
    private final Map<String, TokenData> tokenStore = new ConcurrentHashMap<>();

    /**
     * Genera un nuevo token CSRF único.
     * @return Token CSRF en formato Base64
     */
    public String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        
        // Almacenar con timestamp de expiración
        long expiryTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(TOKEN_VALIDITY_HOURS);
        tokenStore.put(token, new TokenData(expiryTime));
        
        // Limpiar tokens expirados (simple cleanup)
        cleanExpiredTokens();
        
        return token;
    }

    /**
     * Valida si un token CSRF es válido.
     * @param token Token a validar
     * @return true si el token es válido y no ha expirado
     */
    public boolean isValid(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        TokenData data = tokenStore.get(token);
        if (data == null) {
            return false;
        }

        // Verificar si ha expirado
        if (System.currentTimeMillis() > data.getExpiryTime()) {
            tokenStore.remove(token);
            return false;
        }

        return true;
    }

    /**
     * Consume (elimina) un token después de usarlo.
     * Esto previene ataques de replay.
     * @param token Token a consumir
     */
    public void consumeToken(String token) {
        tokenStore.remove(token);
    }

    /**
     * Limpia tokens expirados del almacén.
     */
    private void cleanExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        tokenStore.entrySet().removeIf(entry -> 
            currentTime > entry.getValue().getExpiryTime()
        );
    }

    /**
     * Clase interna para almacenar datos del token.
     */
    private static class TokenData {
        private final long expiryTime;

        public TokenData(long expiryTime) {
            this.expiryTime = expiryTime;
        }

        public long getExpiryTime() {
            return expiryTime;
        }
    }
}

