<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Prueba de Autenticación - JSP</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="bg-gray-100">

    <div class="max-w-6xl mx-auto p-8">
        <h1 class="text-4xl font-bold text-gray-900 mb-8">🧪 Prueba de Autenticación</h1>

        <!-- Botones de Prueba -->
        <div class="bg-white rounded-lg shadow-lg p-6 mb-8">
            <h2 class="text-2xl font-bold text-gray-900 mb-4">Botones de Autenticación</h2>
            <div class="flex space-x-4">
                <button onclick="openLoginModal()" class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition">
                    <i class="fas fa-sign-in-alt mr-2"></i> Abrir Login
                </button>
                <button onclick="openRegisterModal()" class="px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition">
                    <i class="fas fa-user-plus mr-2"></i> Abrir Registro
                </button>
                <button onclick="testAPI()" class="px-6 py-3 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition">
                    <i class="fas fa-vial mr-2"></i> Probar API
                </button>
            </div>
        </div>

        <!-- Estado del Usuario -->
        <div class="bg-white rounded-lg shadow-lg p-6 mb-8">
            <h2 class="text-2xl font-bold text-gray-900 mb-4">Estado del Usuario</h2>
            <div id="user-status" class="text-gray-600">
                No hay usuario logueado
            </div>
            <button onclick="checkUser()" class="mt-4 px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition">
                Verificar Usuario
            </button>
            <button onclick="logoutUser()" class="mt-4 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition">
                Cerrar Sesión
            </button>
        </div>

        <!-- Resultado de las Pruebas -->
        <div class="bg-white rounded-lg shadow-lg p-6">
            <h2 class="text-2xl font-bold text-gray-900 mb-4">Resultado</h2>
            <pre id="result" class="bg-gray-800 text-green-400 p-4 rounded-lg overflow-x-auto min-h-[200px]">Esperando acción...</pre>
        </div>
    </div>

    <!-- Modal de Login -->
    <div id="login-modal" class="hidden fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4">
        <div class="bg-white rounded-2xl max-w-md w-full p-8 relative">
            <button onclick="closeLoginModal()" class="absolute top-4 right-4 text-gray-400 hover:text-gray-600">
                <i class="fas fa-times text-2xl"></i>
            </button>

            <h3 class="text-3xl font-bold text-gray-900 mb-6">Iniciar Sesión</h3>
            <form id="login-form" class="space-y-4">
                <div>
                    <label class="block text-gray-700 mb-2 font-medium">Usuario o Email</label>
                    <input type="text" id="login-username" required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600">
                </div>
                <div>
                    <label class="block text-gray-700 mb-2 font-medium">Contraseña</label>
                    <input type="password" id="login-password" required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600">
                </div>
                <button type="submit"
                        class="w-full py-3 bg-blue-600 text-white rounded-lg font-semibold hover:bg-blue-700 transition">
                    Iniciar Sesión
                </button>
            </form>
            <p class="mt-4 text-center text-gray-600">
                ¿No tienes cuenta?
                <button onclick="switchToRegister()" class="text-blue-600 font-semibold hover:underline">
                    Regístrate
                </button>
            </p>
        </div>
    </div>

    <!-- Modal de Registro -->
    <div id="register-modal" class="hidden fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4">
        <div class="bg-white rounded-2xl max-w-md w-full p-8 relative max-h-[90vh] overflow-y-auto">
            <button onclick="closeRegisterModal()" class="absolute top-4 right-4 text-gray-400 hover:text-gray-600">
                <i class="fas fa-times text-2xl"></i>
            </button>

            <h3 class="text-3xl font-bold text-gray-900 mb-6">Crear Cuenta</h3>
            <form id="register-form" class="space-y-4">
                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="block text-gray-700 mb-2 font-medium">Nombre</label>
                        <input type="text" id="register-nombre"
                               class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-600">
                    </div>
                    <div>
                        <label class="block text-gray-700 mb-2 font-medium">Apellido</label>
                        <input type="text" id="register-apellido"
                               class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-600">
                    </div>
                </div>
                <div>
                    <label class="block text-gray-700 mb-2 font-medium">Usuario *</label>
                    <input type="text" id="register-username" required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-600">
                </div>
                <div>
                    <label class="block text-gray-700 mb-2 font-medium">Email *</label>
                    <input type="email" id="register-email" required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-600">
                </div>
                <div>
                    <label class="block text-gray-700 mb-2 font-medium">Contraseña *</label>
                    <input type="password" id="register-password" required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-600">
                </div>
                <button type="submit"
                        class="w-full py-3 bg-green-600 text-white rounded-lg font-semibold hover:bg-green-700 transition">
                    Crear Cuenta
                </button>
            </form>
            <p class="mt-4 text-center text-gray-600">
                ¿Ya tienes cuenta?
                <button onclick="switchToLogin()" class="text-green-600 font-semibold hover:underline">
                    Inicia sesión
                </button>
            </p>
        </div>
    </div>

    <!-- Toast Notification -->
    <div id="toast" class="hidden fixed bottom-4 right-4 bg-white shadow-2xl rounded-lg p-4 max-w-sm z-50">
        <div class="flex items-center">
            <div id="toast-icon" class="mr-3"></div>
            <div>
                <p id="toast-message" class="font-semibold text-gray-900"></p>
                <p id="toast-detail" class="text-sm text-gray-600"></p>
            </div>
        </div>
    </div>

    <script>
        // Detectar API URL
        const currentPath = window.location.pathname;
        const contextPath = currentPath.substring(0, currentPath.indexOf('/', 1)) || '';
        const API_BASE_URL = contextPath + '/api';

        console.log('Context Path:', contextPath);
        console.log('API Base URL:', API_BASE_URL);

        const result = document.getElementById('result');

        function log(message) {
            const timestamp = new Date().toLocaleTimeString();
            result.textContent += `[${timestamp}] ${message}\n`;
            console.log(message);
        }

        // Funciones del Modal de Login
        function openLoginModal() {
            log('Abriendo modal de login...');
            document.getElementById('login-modal').classList.remove('hidden');
        }

        function closeLoginModal() {
            log('Cerrando modal de login...');
            document.getElementById('login-modal').classList.add('hidden');
        }

        // Funciones del Modal de Registro
        function openRegisterModal() {
            log('Abriendo modal de registro...');
            document.getElementById('register-modal').classList.remove('hidden');
        }

        function closeRegisterModal() {
            log('Cerrando modal de registro...');
            document.getElementById('register-modal').classList.add('hidden');
        }

        // Switch entre modales
        function switchToRegister() {
            closeLoginModal();
            openRegisterModal();
        }

        function switchToLogin() {
            closeRegisterModal();
            openLoginModal();
        }

        // Toast notifications
        function showToast(message, detail = '', type = 'success') {
            const toast = document.getElementById('toast');
            const icon = document.getElementById('toast-icon');
            const messageEl = document.getElementById('toast-message');
            const detailEl = document.getElementById('toast-detail');

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

        // Manejo del formulario de Login
        document.getElementById('login-form').addEventListener('submit', async (e) => {
            e.preventDefault();

            const username = document.getElementById('login-username').value;
            const password = document.getElementById('login-password').value;

            log('Intentando login con usuario: ' + username);

            try {
                const response = await fetch(API_BASE_URL + '/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ usernameOrEmail: username, password })
                });

                const data = await response.json();
                log('Respuesta recibida: ' + JSON.stringify(data, null, 2));

                if (response.ok) {
                    localStorage.setItem('user', JSON.stringify(data));
                    showToast('¡Éxito!', 'Sesión iniciada correctamente', 'success');
                    closeLoginModal();
                    checkUser();
                } else {
                    showToast('Error', data.message || 'Credenciales incorrectas', 'error');
                    log('Error en login: ' + (data.message || 'Credenciales incorrectas'));
                }
            } catch (error) {
                log('ERROR: ' + error.message);
                showToast('Error', 'No se pudo conectar con el servidor', 'error');
            }
        });

        // Manejo del formulario de Registro
        document.getElementById('register-form').addEventListener('submit', async (e) => {
            e.preventDefault();

            const username = document.getElementById('register-username').value;
            const email = document.getElementById('register-email').value;
            const password = document.getElementById('register-password').value;
            const nombre = document.getElementById('register-nombre').value;
            const apellido = document.getElementById('register-apellido').value;

            const payload = {
                username,
                email,
                password,
                nombre: nombre || undefined,
                apellido: apellido || undefined
            };

            log('Intentando registro: ' + JSON.stringify(payload, null, 2));

            try {
                const response = await fetch(API_BASE_URL + '/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                const data = await response.json();
                log('Respuesta recibida: ' + JSON.stringify(data, null, 2));

                if (response.ok) {
                    showToast('¡Cuenta creada!', 'Ahora puedes iniciar sesión', 'success');
                    switchToLogin();
                } else {
                    showToast('Error', data.message || 'Error al crear cuenta', 'error');
                    log('Error en registro: ' + (data.message || 'Error desconocido'));
                }
            } catch (error) {
                log('ERROR: ' + error.message);
                showToast('Error', 'No se pudo conectar con el servidor', 'error');
            }
        });

        // Verificar usuario logueado
        function checkUser() {
            const user = localStorage.getItem('user');
            const statusDiv = document.getElementById('user-status');

            if (user) {
                const userData = JSON.parse(user);
                statusDiv.innerHTML = `
                    <div class="bg-green-50 border border-green-200 rounded-lg p-4">
                        <p class="text-green-800 font-bold">✅ Usuario logueado</p>
                        <p class="text-sm text-green-700 mt-2">Username: ${userData.username}</p>
                        <p class="text-sm text-green-700">Email: ${userData.email || 'N/A'}</p>
                        <p class="text-sm text-green-700">Rol: ${userData.role || 'N/A'}</p>
                    </div>
                `;
                log('Usuario encontrado en localStorage: ' + JSON.stringify(userData, null, 2));
            } else {
                statusDiv.innerHTML = `
                    <div class="bg-gray-50 border border-gray-200 rounded-lg p-4">
                        <p class="text-gray-600">❌ No hay usuario logueado</p>
                    </div>
                `;
                log('No hay usuario en localStorage');
            }
        }

        // Cerrar sesión
        function logoutUser() {
            localStorage.removeItem('user');
            log('Usuario deslogueado');
            showToast('Sesión cerrada', '', 'info');
            checkUser();
        }

        // Probar API
        async function testAPI() {
            log('Probando conexión con la API...');
            try {
                const response = await fetch(API_BASE_URL + '/recetas');
                const data = await response.json();
                log('API respondió correctamente: ' + data.length + ' recetas encontradas');
                showToast('API Funciona', data.length + ' recetas encontradas', 'success');
            } catch (error) {
                log('ERROR al probar API: ' + error.message);
                showToast('Error', 'API no responde', 'error');
            }
        }

        // Inicializar
        window.addEventListener('DOMContentLoaded', () => {
            log('Página de prueba cargada');
            log('API URL configurada: ' + API_BASE_URL);
            checkUser();
        });
    </script>
</body>
</html>
