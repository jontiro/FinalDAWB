<%-- auth-modals.jsp - Modales de autenticación reutilizables --%>

<!-- Modal de Login -->
<div id="auth-modal" class="hidden fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl max-w-md w-full p-8 relative">
        <button onclick="closeAuthModal()" class="absolute top-4 right-4 text-gray-400 hover:text-gray-600">
            <i class="fas fa-times text-2xl"></i>
        </button>

        <div id="login-form-container">
            <h3 class="text-3xl font-bold text-gray-900 mb-6">Iniciar Sesión</h3>
            <form id="login-form" class="space-y-4">
                <div>
                    <label class="block text-gray-700 mb-2 font-medium">Usuario o Email</label>
                    <input type="text" id="login-username" required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-600 focus:border-transparent">
                </div>
                <div>
                    <label class="block text-gray-700 mb-2 font-medium">Contraseña</label>
                    <input type="password" id="login-password" required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-600 focus:border-transparent">
                </div>
                <button type="submit"
                        class="w-full py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-indigo-700 transition">
                    Iniciar Sesión
                </button>
            </form>
            <p class="mt-4 text-center text-gray-600">
                ¿No tienes cuenta?
                <button onclick="openAuthModal('register')" class="text-purple-600 font-semibold hover:underline">
                    Regístrate
                </button>
            </p>
        </div>

        <div id="register-form-container" class="hidden">
            <h3 class="text-3xl font-bold text-gray-900 mb-6">Crear Cuenta</h3>
            <form id="register-form" class="space-y-4">
                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="block text-gray-700 mb-2 font-medium">Nombre</label>
                        <input type="text" id="register-nombre"
                               class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-600 focus:border-transparent">
                    </div>
                    <div>
                        <label class="block text-gray-700 mb-2 font-medium">Apellido</label>
                        <input type="text" id="register-apellido"
                               class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-600 focus:border-transparent">
                    </div>
                </div>
                <div>
                    <label class="block text-gray-700 mb-2 font-medium">Usuario</label>
                    <input type="text" id="register-username" required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-600 focus:border-transparent">
                </div>
                <div>
                    <label class="block text-gray-700 mb-2 font-medium">Email</label>
                    <input type="email" id="register-email" required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-600 focus:border-transparent">
                </div>
                <div>
                    <label class="block text-gray-700 mb-2 font-medium">Contraseña</label>
                    <input type="password" id="register-password" required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-600 focus:border-transparent">
                </div>
                <button type="submit"
                        class="w-full py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-indigo-700 transition">
                    Crear Cuenta
                </button>
            </form>
            <p class="mt-4 text-center text-gray-600">
                ¿Ya tienes cuenta?
                <button onclick="openAuthModal('login')" class="text-purple-600 font-semibold hover:underline">
                    Inicia sesión
                </button>
            </p>
        </div>
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
