// auth.js - Sistema de autenticación simplificado
const API_BASE_URL = (() => {
    const currentPath = window.location.pathname;
    const contextPath = currentPath.substring(0, currentPath.indexOf('/', 1)) || '';
    return `${contextPath}/api`;
})();

const Auth = {
    // Obtener usuario logueado
    getUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    },

    // Guardar usuario
    setUser(userData) {
        localStorage.setItem('user', JSON.stringify(userData));
    },

    // Cerrar sesión
    logout() {
        localStorage.removeItem('user');
        window.location.href = 'home.html';
    },

    // Verificar si está logueado
    isLoggedIn() {
        return this.getUser() !== null;
    },

    // Verificar si es admin
    isAdmin() {
        const user = this.getUser();
        return user && user.role === 'ADMIN';
    },

    // Actualizar navegación según usuario
    updateNavigation() {
        const navButtons = document.getElementById('nav-buttons');
        if (!navButtons) return;

        const user = this.getUser();

        if (user) {
            navButtons.innerHTML = `
                <div class="flex items-center space-x-4">
                    ${user.role === 'ADMIN' ? '<a href="admin.html" class="text-gray-700 hover:text-purple-600 transition"><i class="fas fa-cog mr-1"></i> Admin</a>' : ''}
                    <span class="text-gray-700">Hola, <strong>${user.username}</strong></span>
                    <button onclick="Auth.logout()" class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition">
                        Cerrar Sesión
                    </button>
                </div>
            `;
        } else {
            navButtons.innerHTML = `
                <button onclick="openAuthModal('login')" class="px-4 py-2 text-purple-600 border border-purple-600 rounded-lg hover:bg-purple-50 transition">
                    Iniciar Sesión
                </button>
                <button onclick="openAuthModal('register')" class="px-4 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg hover:from-purple-700 hover:to-indigo-700 transition">
                    Registrarse
                </button>
            `;
        }
    }
};

// Funciones globales para los modales (llamadas desde HTML con onclick)
function openAuthModal(type) {
    const modal = document.getElementById('auth-modal');
    if (!modal) return;
    
    modal.classList.remove('hidden');
    
    if (type === 'login') {
        document.getElementById('login-form-container').classList.remove('hidden');
        document.getElementById('register-form-container').classList.add('hidden');
    } else {
        document.getElementById('login-form-container').classList.add('hidden');
        document.getElementById('register-form-container').classList.remove('hidden');
    }
}

function closeAuthModal() {
    const modal = document.getElementById('auth-modal');
    if (modal) modal.classList.add('hidden');
}

// Handler de login
async function handleLogin(e) {
    e.preventDefault();
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ usernameOrEmail: username, password })
        });

        const data = await response.json();

        if (response.ok) {
            Auth.setUser(data);
            Utils.showToast('¡Bienvenido!', `Sesión iniciada como ${data.username}`, 'success');
            closeAuthModal();
            Auth.updateNavigation();
            
            // Redireccionar según rol
            if (data.role === 'ADMIN') {
                setTimeout(() => window.location.href = 'admin.html', 1000);
            }
        } else {
            Utils.showToast('Error de autenticación', data.message || 'Credenciales incorrectas', 'error');
        }
    } catch (error) {
        Utils.showToast('Error', 'No se pudo conectar con el servidor', 'error');
    }
}

// Handler de registro
async function handleRegister(e) {
    e.preventDefault();
    const payload = {
        username: document.getElementById('register-username').value,
        email: document.getElementById('register-email').value,
        password: document.getElementById('register-password').value,
        nombre: document.getElementById('register-nombre').value || undefined,
        apellido: document.getElementById('register-apellido').value || undefined
    };

    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.ok) {
            Utils.showToast('¡Cuenta creada!', `Bienvenido ${data.username}. Ahora inicia sesión`, 'success');
            openAuthModal('login');
        } else {
            Utils.showToast('Error al registrarse', data.message || 'Verifica los datos', 'error');
        }
    } catch (error) {
        Utils.showToast('Error', 'No se pudo conectar con el servidor', 'error');
    }
}

// Utilidades
const Utils = {
    showToast(message, detail = '', type = 'success') {
        const toast = document.getElementById('toast');
        const icon = document.getElementById('toast-icon');
        const messageEl = document.getElementById('toast-message');
        const detailEl = document.getElementById('toast-detail');
        
        if (!toast) return;
        
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
    },

    animateCounter(id, target) {
        const element = document.getElementById(id);
        if (!element) return;
        
        let current = 0;
        const increment = target / 50;
        const timer = setInterval(() => {
            current += increment;
            if (current >= target) {
                element.textContent = target;
                clearInterval(timer);
            } else {
                element.textContent = Math.floor(current);
            }
        }, 30);
    }
};

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    Auth.updateNavigation();
    
    // Adjuntar event listeners a los formularios si existen
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    const registerForm = document.getElementById('register-form');
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }
});
