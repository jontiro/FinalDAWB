/**
 * Módulo de Protección CSRF
 * Maneja la obtención, almacenamiento y envío automático de tokens CSRF
 *
 * IMPORTANTE: Se inicializa DESPUÉS del login, no antes
 */

const CsrfProtection = (() => {
    const CSRF_TOKEN_KEY = 'csrf_token';
    const TOKEN_REFRESH_HOURS = 23; // Refrescar antes de expirar (24h)

    /**
     * Obtiene un nuevo token CSRF del servidor
     */
    async function fetchCsrfToken() {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/csrf-token`);
            if (!response.ok) {
                throw new Error('Error al obtener token CSRF');
            }
            const data = await response.json();
            
            // Almacenar token con timestamp
            const tokenData = {
                token: data.token,
                timestamp: Date.now()
            };
            localStorage.setItem(CSRF_TOKEN_KEY, JSON.stringify(tokenData));
            
            console.log('✅ Token CSRF obtenido y almacenado');
            return data.token;
        } catch (error) {
            console.error('❌ Error al obtener token CSRF:', error);
            return null;
        }
    }

    /**
     * Obtiene el token CSRF almacenado o solicita uno nuevo si es necesario
     */
    async function getCsrfToken() {
        const storedData = localStorage.getItem(CSRF_TOKEN_KEY);
        
        if (storedData) {
            try {
                const { token, timestamp } = JSON.parse(storedData);
                const hoursSinceObtained = (Date.now() - timestamp) / (1000 * 60 * 60);

                // Si el token tiene menos de 23 horas, usarlo
                if (hoursSinceObtained < TOKEN_REFRESH_HOURS) {
                    return token;
                }
            } catch (e) {
                console.warn('Token CSRF corrupto, obteniendo uno nuevo');
            }
        }
        
        // Obtener nuevo token
        return await fetchCsrfToken();
    }

    /**
     * Realiza un fetch con protección CSRF automática
     */
    async function protectedFetch(url, options = {}) {
        const method = options.method || 'GET';
        
        // Solo agregar token en métodos de escritura
        if (['POST', 'PUT', 'DELETE', 'PATCH'].includes(method.toUpperCase())) {
            const csrfToken = await getCsrfToken();
            
            if (!csrfToken) {
                console.warn('⚠️ No se pudo obtener token CSRF, la petición puede fallar');
            } else {
                // Agregar token al header
                options.headers = {
                    ...options.headers,
                    'X-CSRF-Token': csrfToken
                };
            }
        }
        
        // Realizar fetch normal
        const response = await fetch(url, options);
        
        // Si recibimos error 403, puede ser token expirado - reintentar una vez
        if (response.status === 403 && ['POST', 'PUT', 'DELETE', 'PATCH'].includes(method.toUpperCase())) {
            console.warn('⚠️ Token CSRF rechazado, obteniendo uno nuevo...');
            
            // Forzar nuevo token
            localStorage.removeItem(CSRF_TOKEN_KEY);
            const newToken = await fetchCsrfToken();
            
            if (newToken) {
                options.headers['X-CSRF-Token'] = newToken;
                // Reintentar la petición
                return await fetch(url, options);
            }
        }
        
        return response;
    }

    /**
     * Inicializa la protección CSRF (llamar después del login exitoso)
     */
    async function initialize() {
        console.log('🔒 Inicializando protección CSRF...');
        await getCsrfToken();
    }

    /**
     * Limpia el token CSRF (útil al hacer logout)
     */
    function clearToken() {
        localStorage.removeItem(CSRF_TOKEN_KEY);
        console.log('🗑️ Token CSRF eliminado');
    }

    // API pública
    return {
        initialize,
        getCsrfToken,
        protectedFetch,
        clearToken
    };
})();

// NO inicializar automáticamente - esperar a que el usuario haga login
// La inicialización se hará en auth.js después del login exitoso

