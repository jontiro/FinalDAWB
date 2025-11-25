// components.js - Componentes reutilizables
const Components = {
    loadNavigation() {
        // Ya se carga en Auth.updateNavigation()
    },

    loadFooter() {
        const footer = document.getElementById('footer');
        if (!footer) return;

        footer.innerHTML = `
            <footer class="bg-gray-900 text-white py-12">
                <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div class="grid md:grid-cols-4 gap-8 mb-8">
                        <div>
                            <div class="flex items-center mb-4">
                                <i class="fas fa-utensils text-3xl text-purple-500 mr-3"></i>
                                <span class="text-2xl font-bold">Cocina Social</span>
                            </div>
                            <p class="text-gray-400">
                                La plataforma definitiva para amantes de la cocina.
                            </p>
                        </div>
                        <div>
                            <h4 class="font-bold text-lg mb-4">Explora</h4>
                            <ul class="space-y-2 text-gray-400">
                                <li><a href="recetas.html" class="hover:text-white transition">Recetas</a></li>
                                <li><a href="lugares.html" class="hover:text-white transition">Lugares</a></li>
                                <li><a href="comunidad.html" class="hover:text-white transition">Comunidad</a></li>
                                <li><a href="index.html" class="hover:text-white transition">Panel de Pruebas</a></li>
                            </ul>
                        </div>
                        <div>
                            <h4 class="font-bold text-lg mb-4">Soporte</h4>
                            <ul class="space-y-2 text-gray-400">
                                <li><a href="#" class="hover:text-white transition">Centro de Ayuda</a></li>
                                <li><a href="#" class="hover:text-white transition">Términos de Uso</a></li>
                                <li><a href="#" class="hover:text-white transition">Privacidad</a></li>
                                <li><a href="#" class="hover:text-white transition">API Docs</a></li>
                            </ul>
                        </div>
                        <div>
                            <h4 class="font-bold text-lg mb-4">Síguenos</h4>
                            <div class="flex space-x-4">
                                <a href="#" class="w-10 h-10 bg-purple-600 rounded-full flex items-center justify-center hover:bg-purple-700 transition">
                                    <i class="fab fa-facebook-f"></i>
                                </a>
                                <a href="#" class="w-10 h-10 bg-purple-600 rounded-full flex items-center justify-center hover:bg-purple-700 transition">
                                    <i class="fab fa-instagram"></i>
                                </a>
                                <a href="#" class="w-10 h-10 bg-purple-600 rounded-full flex items-center justify-center hover:bg-purple-700 transition">
                                    <i class="fab fa-twitter"></i>
                                </a>
                                <a href="#" class="w-10 h-10 bg-purple-600 rounded-full flex items-center justify-center hover:bg-purple-700 transition">
                                    <i class="fab fa-youtube"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                    <div class="border-t border-gray-800 pt-8 text-center text-gray-400">
                        <p>&copy; 2025 Cocina Social. Proyecto FinalDAWB</p>
                    </div>
                </div>
            </footer>
        `;
    },

    loadAuthModal() {
        const modal = document.getElementById('auth-modal');
        if (!modal) return;

        modal.innerHTML = `
            <div class="hidden fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4" id="auth-modal">
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
        `;
    },

    loadToast() {
        const toastContainer = document.getElementById('toast');
        if (!toastContainer) return;

        toastContainer.className = 'hidden fixed bottom-4 right-4 bg-white shadow-2xl rounded-lg p-4 max-w-sm z-50';
        toastContainer.innerHTML = `
                <div class="flex items-center">
                    <div id="toast-icon" class="mr-3"></div>
                    <div>
                        <p id="toast-message" class="font-semibold text-gray-900"></p>
                        <p id="toast-detail" class="text-sm text-gray-600"></p>
                    </div>
                </div>
        `;
    }
};
