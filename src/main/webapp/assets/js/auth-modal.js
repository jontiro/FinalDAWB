// auth-modal.js - Sistema de autenticación con modales
// Usar API_BASE_URL de window (ya definida por auth.js)
if (!window.API_BASE_URL) {
    window.API_BASE_URL = (() => {
        const currentPath = window.location.pathname;
        const contextPath = currentPath.substring(0, currentPath.indexOf('/', 1)) || '';
        return contextPath + '/api';
    })();
}

console.log('Auth Modal - API Base URL:', window.API_BASE_URL);

// ==================== GESTIÓN DE MODALES ====================

/**
 * Abre el modal de login
 */
function openLoginModal() {
    const modal = document.getElementById('login-modal');
    if (modal) {
        modal.classList.remove('hidden');
    }
}

/**
 * Cierra el modal de login
 */
function closeLoginModal() {
    const modal = document.getElementById('login-modal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

/**
 * Abre el modal de registro
 */
function openRegisterModal() {
    const modal = document.getElementById('register-modal');
    if (modal) {
        modal.classList.remove('hidden');
    }
}

/**
 * Cierra el modal de registro
 */
function closeRegisterModal() {
    const modal = document.getElementById('register-modal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

/**
 * Cambia del modal de login al de registro
 */
function switchToRegister() {
    closeLoginModal();
    openRegisterModal();
}

/**
 * Cambia del modal de registro al de login
 */
function switchToLogin() {
    closeRegisterModal();
    openLoginModal();
}

// ==================== GESTIÓN DE USUARIO ====================

/**
 * Obtiene el usuario actual del localStorage
 * @returns {Object|null} Datos del usuario o null si no está logueado
 */
function getUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
}

/**
 * Guarda el usuario en localStorage
 * @param {Object} userData - Datos del usuario
 */
function setUser(userData) {
    localStorage.setItem('user', JSON.stringify(userData));
}

/**
 * Elimina el usuario del localStorage
 */
function removeUser() {
    localStorage.removeItem('user');
}

/**
 * Cierra la sesión del usuario
 */
function logout() {
    removeUser();

    // 🔒 Limpiar token CSRF al cerrar sesión
    if (typeof CsrfProtection !== 'undefined') {
        CsrfProtection.clearToken();
    }

    showToast('Sesión cerrada', '', 'info');
    updateNavButtons();
    
    // Opcional: redirigir a home
    // window.location.href = 'home.html';
}

/**
 * Actualiza los botones de navegación según el estado de autenticación
 */
function updateNavButtons() {
    const navButtons = document.getElementById('nav-buttons');
    if (!navButtons) return;

    const user = getUser();

    if (user) {
        navButtons.innerHTML = `
            <div class="flex items-center space-x-4">
                ${user.role === 'ADMIN' ? '<a href="admin.html" class="text-gray-700 hover:text-purple-600 transition"><i class="fas fa-cog mr-1"></i> Admin</a>' : ''}
                <span class="text-gray-700">Hola, <strong>${user.username}</strong></span>
                <button onclick="logout()" class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition">
                    Cerrar Sesión
                </button>
            </div>
        `;
    } else {
        navButtons.innerHTML = `
            <button onclick="openLoginModal()" class="px-4 py-2 text-purple-600 border border-purple-600 rounded-lg hover:bg-purple-50 transition">
                Iniciar Sesión
            </button>
            <button onclick="openRegisterModal()" class="px-4 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg hover:from-purple-700 hover:to-indigo-700 transition">
                Registrarse
            </button>
        `;
    }
}

// ==================== NOTIFICACIONES ====================

/**
 * Muestra una notificación toast
 * @param {string} message - Mensaje principal
 * @param {string} detail - Detalle adicional (opcional)
 * @param {string} type - Tipo: 'success', 'error', 'info'
 */
function showToast(message, detail = '', type = 'success') {
    const toast = document.getElementById('toast');
    const icon = document.getElementById('toast-icon');
    const messageEl = document.getElementById('toast-message');
    const detailEl = document.getElementById('toast-detail');

    if (!toast) {
        console.warn('Toast element not found');
        return;
    }

    const icons = {
        success: '<i class="fas fa-check-circle text-green-500 text-2xl"></i>',
        error: '<i class="fas fa-exclamation-circle text-red-500 text-2xl"></i>',
        info: '<i class="fas fa-info-circle text-blue-500 text-2xl"></i>'
    };

    icon.innerHTML = icons[type] || icons.info;
    messageEl.textContent = message;
    detailEl.textContent = detail;

    toast.classList.remove('hidden');
    setTimeout(() => toast.classList.add('hidden'), 5000);
}

// ==================== AUTENTICACIÓN ====================

/**
 * Maneja el envío del formulario de login
 * @param {Event} e - Evento del formulario
 */
async function handleLoginSubmit(e) {
    e.preventDefault();

    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    console.log('Intentando login con usuario:', username);

    try {
        const response = await fetch(window.API_BASE_URL + '/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ usernameOrEmail: username, password })
        });

        const data = await response.json();
        console.log('Respuesta del servidor:', data);

        if (response.ok) {
            setUser(data);
            showToast('¡Bienvenido!', `Sesión iniciada como ${data.username}`, 'success');
            closeLoginModal();
            updateNavButtons();
            
            // 🔒 Inicializar protección CSRF después del login exitoso
            if (typeof CsrfProtection !== 'undefined') {
                CsrfProtection.initialize().then(() => {
                    console.log('✅ Protección CSRF activada después del login');
                }).catch(err => {
                    console.warn('⚠️ No se pudo inicializar CSRF:', err);
                });
            }

            // Redireccionar si es admin
            if (data.role === 'ADMIN') {
                setTimeout(() => window.location.href = 'admin.html', 1000);
            }
        } else {
            showToast('Error', data.message || 'Credenciales incorrectas', 'error');
            console.error('Error en login:', data.message);
        }
    } catch (error) {
        console.error('Error de conexión:', error);
        showToast('Error', 'No se pudo conectar con el servidor', 'error');
    }
}

/**
 * Maneja el envío del formulario de registro
 * @param {Event} e - Evento del formulario
 */
async function handleRegisterSubmit(e) {
    e.preventDefault();

    const username = document.getElementById('register-username').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;


    const payload = {
        username,
        email,
        password,
    };

    console.log('Intentando registro:', payload.username);

    try {
        const response = await fetch(window.API_BASE_URL + '/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();
        console.log('Respuesta del servidor:', data);

        if (response.ok) {
            showToast('¡Cuenta creada!', 'Ahora puedes iniciar sesión', 'success');
            switchToLogin();
            
            // Limpiar formulario
            document.getElementById('register-form').reset();
        } else {
            showToast('Error', data.message || 'Error al crear cuenta', 'error');
            console.error('Error en registro:', data.message);
        }
    } catch (error) {
        console.error('Error de conexión:', error);
        showToast('Error', 'No se pudo conectar con el servidor', 'error');
    }
}

// ==================== INICIALIZACIÓN ====================

/**
 * Inicializa el sistema de autenticación
 * Adjunta los event listeners a los formularios
 */
function initAuthModal() {
    console.log('Inicializando sistema de autenticación modal...');
    
    // Actualizar navegación según usuario
    updateNavButtons();

    // Adjuntar event listener al formulario de login
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLoginSubmit);
        console.log('✓ Event listener de login adjuntado');
    } else {
        console.warn('⚠ Formulario de login no encontrado');
    }

    // Adjuntar event listener al formulario de registro
    const registerForm = document.getElementById('register-form');
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegisterSubmit);
        console.log('✓ Event listener de registro adjuntado');
    } else {
        console.warn('⚠ Formulario de registro no encontrado');
    }

    console.log('✓ Sistema de autenticación inicializado');
}

// Inicializar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAuthModal);
} else {
    // DOM ya está listo
    initAuthModal();
}

