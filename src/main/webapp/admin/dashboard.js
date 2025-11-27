// dashboard.js - Panel de Administración
console.log('📊 Dashboard.js cargado correctamente');

// Verificar que API_BASE_URL esté disponible
if (!window.API_BASE_URL) {
    console.error('❌ API_BASE_URL no está definida');
} else {
    console.log('✅ API_BASE_URL disponible:', window.API_BASE_URL);
}

let currentUsuarioId = null;
let roles = [];
let chartUsuarios, chartRecetas, chartContenido;

// Verificar autenticación y permisos de admin
function checkAdmin() {
    const user = getUser();
    if (!user || user.role !== 'ADMIN') {
        alert('Acceso denegado. Solo administradores pueden acceder a esta página.');
        window.location.href = '../home.html';
    }
}

function getUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
}

// Utilidades
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function sanitizeNumber(value) {
    const num = parseInt(value);
    return isNaN(num) ? 0 : num;
}

function showToast(title, message, type = 'info') {
    const colors = {
        success: 'bg-green-600',
        error: 'bg-red-600',
        info: 'bg-blue-600'
    };
    const toast = document.createElement('div');
    toast.className = `fixed bottom-4 right-4 ${colors[type]} text-white px-6 py-4 rounded-lg shadow-lg z-50`;
    toast.innerHTML = `<div class="flex items-center">
        <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'} mr-3"></i>
        <div><p class="font-bold">${title}</p><p class="text-sm">${message}</p></div>
    </div>`;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

// ===== TABS =====
window.showTab = function(tabName) {
    document.querySelectorAll('.tab-content').forEach(tab => tab.classList.add('hidden'));
    document.querySelectorAll('.admin-tab').forEach(btn => {
        btn.classList.remove('active', 'border-blue-600', 'text-blue-600');
        btn.classList.add('border-transparent', 'text-gray-500');
    });

    document.getElementById(`tab-${tabName}`).classList.remove('hidden');
    const activeTab = Array.from(document.querySelectorAll('.admin-tab')).find(btn =>
        btn.textContent.toLowerCase().includes(tabName)
    );
    if (activeTab) {
        activeTab.classList.add('active', 'border-blue-600', 'text-blue-600');
        activeTab.classList.remove('border-transparent', 'text-gray-500');
    }

    if (tabName === 'comentarios') loadComentarios();
    else if (tabName === 'graficas') loadGraficas();
    else if (tabName === 'usuarios') loadUsuarios();
};
console.log('✅ showTab definida');

// ===== ESTADÍSTICAS =====
async function loadStats() {
    try {
        const [recetas, lugares, comentarios, usuariosData] = await Promise.all([
            fetch(`${window.API_BASE_URL}/recetas`).then(r => r.ok ? r.json() : []).catch(() => []),
            fetch(`${window.API_BASE_URL}/lugares`).then(r => r.ok ? r.json() : []).catch(() => []),
            fetch(`${window.API_BASE_URL}/comentarios`).then(r => r.ok ? r.json() : []).catch(() => []),
            fetch(`${window.API_BASE_URL}/auth/count`).then(r => r.ok ? r.json() : null).catch(() => null)
        ]);

        document.getElementById('stat-recetas').textContent = recetas.length;
        document.getElementById('stat-lugares').textContent = lugares.length;
        document.getElementById('stat-comentarios').textContent = comentarios.length;
        if (usuariosData?.count) document.getElementById('stat-usuarios').textContent = usuariosData.count;
    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

// ===== COMENTARIOS =====
window.loadComentarios = async function() {
    const tbody = document.getElementById('comentarios-table');
    tbody.innerHTML = '<tr><td colspan="6" class="px-6 py-4 text-center"><i class="fas fa-spinner fa-spin"></i> Cargando...</td></tr>';

    try {
        const response = await fetch(`${window.API_BASE_URL}/comentarios`);
        if (!response.ok) throw new Error('Error');

        const comentarios = await response.json();

        if (!Array.isArray(comentarios) || comentarios.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="px-6 py-4 text-center text-gray-500">No hay comentarios</td></tr>';
            return;
        }

        tbody.innerHTML = comentarios.map(c => `
            <tr class="hover:bg-gray-50">
                <td class="px-6 py-4 text-sm font-medium">${sanitizeNumber(c.id)}</td>
                <td class="px-6 py-4 text-sm">${escapeHtml(c.autorUsername) || 'N/A'}</td>
                <td class="px-6 py-4 text-sm max-w-xs truncate">${escapeHtml(c.texto)}</td>
                <td class="px-6 py-4 text-sm">${c.recetaId ? `Receta #${c.recetaId}` : `Lugar #${c.lugarId}`}</td>
                <td class="px-6 py-4 text-sm">${c.fechaCreacion ? new Date(c.fechaCreacion).toLocaleDateString('es-ES') : 'N/A'}</td>
                <td class="px-6 py-4 text-center space-x-2">
                    <button onclick="ocultarComentario(${c.id})"
                            class="px-3 py-1 bg-yellow-600 text-white text-xs rounded hover:bg-yellow-700">
                        <i class="fas fa-eye-slash"></i> Ocultar
                    </button>
                    <button onclick="eliminarComentario(${c.id})"
                            class="px-3 py-1 bg-red-600 text-white text-xs rounded hover:bg-red-700">
                        <i class="fas fa-trash"></i> Borrar
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        tbody.innerHTML = '<tr><td colspan="6" class="px-6 py-4 text-center text-red-600">Error al cargar</td></tr>';
    }
};
console.log('✅ loadComentarios definida');

window.ocultarComentario = async function(id) {
    if (!confirm('¿Ocultar este comentario?')) return;
    try {
        const response = await CsrfProtection.protectedFetch(`${window.API_BASE_URL}/admin/comentarios/${id}/rechazar`, {
            method: 'PUT'
        });
        if (response.ok) {
            showToast('Ocultado', 'Comentario ocultado', 'success');
            loadComentarios();
        } else showToast('Error', 'No se pudo ocultar', 'error');
    } catch (error) {
        showToast('Error', 'No se pudo ocultar', 'error');
    }
};

window.eliminarComentario = async function(id) {
    if (!confirm('¿Eliminar permanentemente?')) return;
    try {
        const response = await CsrfProtection.protectedFetch(`${window.API_BASE_URL}/comentarios/${id}`, {
            method: 'DELETE'
        });
        if (response.ok) {
            showToast('Eliminado', 'Comentario eliminado', 'success');
            loadComentarios();
            loadStats();
        } else showToast('Error', 'No se pudo eliminar', 'error');
    } catch (error) {
        showToast('Error', 'No se pudo eliminar', 'error');
    }
};

// ===== GRÁFICAS =====
window.loadGraficas = async function() {
    if (chartUsuarios) chartUsuarios.destroy();
    if (chartRecetas) chartRecetas.destroy();
    if (chartContenido) chartContenido.destroy();

    const labels = [];
    const today = new Date();
    for (let i = 6; i >= 0; i--) {
        const date = new Date(today);
        date.setDate(date.getDate() - i);
        labels.push(date.toLocaleDateString('es-ES', { day: '2-digit', month: 'short' }));
    }

    const ctxUsuarios = document.getElementById('chartUsuarios').getContext('2d');
    chartUsuarios = new Chart(ctxUsuarios, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Usuarios Activos',
                data: [12, 19, 15, 25, 22, 30, 35],
                borderColor: 'rgb(59, 130, 246)',
                backgroundColor: 'rgba(59, 130, 246, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true } }
        }
    });

    const ctxRecetas = document.getElementById('chartRecetas').getContext('2d');
    chartRecetas = new Chart(ctxRecetas, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Recetas Publicadas',
                data: [3, 5, 2, 8, 4, 6, 7],
                backgroundColor: 'rgba(251, 146, 60, 0.8)',
                borderColor: 'rgb(251, 146, 60)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true } }
        }
    });

    try {
        const [recetas, lugares, comentarios] = await Promise.all([
            fetch(`${window.API_BASE_URL}/recetas`).then(r => r.ok ? r.json() : []),
            fetch(`${window.API_BASE_URL}/lugares`).then(r => r.ok ? r.json() : []),
            fetch(`${window.API_BASE_URL}/comentarios`).then(r => r.ok ? r.json() : [])
        ]);

        const ctxContenido = document.getElementById('chartContenido').getContext('2d');
        chartContenido = new Chart(ctxContenido, {
            type: 'doughnut',
            data: {
                labels: ['Recetas', 'Lugares', 'Comentarios'],
                datasets: [{
                    data: [recetas.length, lugares.length, comentarios.length],
                    backgroundColor: ['rgba(251, 146, 60, 0.8)', 'rgba(236, 72, 153, 0.8)', 'rgba(20, 184, 166, 0.8)']
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { position: 'bottom' } }
            }
        });
    } catch (error) {
        console.error('Error loading charts:', error);
    }
};

// ===== USUARIOS =====
window.loadUsuarios = async function() {
    const tbody = document.getElementById('usuarios-table');
    tbody.innerHTML = '<tr><td colspan="6" class="px-6 py-4 text-center"><i class="fas fa-spinner fa-spin"></i> Cargando...</td></tr>';

    try {
        const response = await fetch(`${window.API_BASE_URL}/admin/usuarios`);
        if (!response.ok) throw new Error('Error');

        const usuarios = await response.json();

        if (!Array.isArray(usuarios) || usuarios.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="px-6 py-4 text-center text-gray-500">No hay usuarios</td></tr>';
            return;
        }

        tbody.innerHTML = usuarios.map(u => {
            const estadoBadge = u.estado === 'ACTIVO'
                ? '<span class="px-2 py-1 text-xs bg-green-100 text-green-800 rounded-full">Activo</span>'
                : '<span class="px-2 py-1 text-xs bg-red-100 text-red-800 rounded-full">Bloqueado</span>';

            const rolBadge = u.role === 'ADMIN'
                ? '<span class="px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded-full">Admin</span>'
                : '<span class="px-2 py-1 text-xs bg-gray-100 text-gray-800 rounded-full">Usuario</span>';

            return `
                <tr class="hover:bg-gray-50">
                    <td class="px-6 py-4 text-sm font-medium">${sanitizeNumber(u.id)}</td>
                    <td class="px-6 py-4 text-sm">${escapeHtml(u.username)}</td>
                    <td class="px-6 py-4 text-sm">${escapeHtml(u.email)}</td>
                    <td class="px-6 py-4 text-sm">${rolBadge}</td>
                    <td class="px-6 py-4 text-sm">${estadoBadge}</td>
                    <td class="px-6 py-4 text-center">
                        <div class="flex justify-center space-x-2">
                            <button onclick="verUsuario(${u.id})" class="px-2 py-1 bg-blue-600 text-white text-xs rounded hover:bg-blue-700">
                                <i class="fas fa-eye"></i>
                            </button>
                            <button onclick="editUsuario(${u.id})" class="px-2 py-1 bg-yellow-600 text-white text-xs rounded hover:bg-yellow-700">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button onclick="deleteUsuario(${u.id})" class="px-2 py-1 bg-red-600 text-white text-xs rounded hover:bg-red-700">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (error) {
        tbody.innerHTML = '<tr><td colspan="6" class="px-6 py-4 text-center text-red-600">Error al cargar</td></tr>';
    }
};

window.verUsuario = async function(id) {
    try {
        const response = await fetch(`${window.API_BASE_URL}/admin/usuarios/${id}`);
        if (!response.ok) throw new Error('Error');

        const u = await response.json();
        const content = `
            <div class="space-y-4">
                <div class="grid grid-cols-2 gap-4">
                    <div><p class="text-sm text-gray-500">ID</p><p class="text-lg font-semibold">#${u.id}</p></div>
                    <div><p class="text-sm text-gray-500">Usuario</p><p class="text-lg font-semibold">${escapeHtml(u.username)}</p></div>
                </div>
                <div><p class="text-sm text-gray-500">Email</p><p class="text-lg">${escapeHtml(u.email)}</p></div>
                <div class="grid grid-cols-2 gap-4">
                    <div><p class="text-sm text-gray-500">Rol</p><p class="text-lg font-semibold ${u.roleNombre === 'ADMIN' ? 'text-blue-600' : 'text-gray-600'}">${u.roleNombre || 'N/A'}</p></div>
                    <div><p class="text-sm text-gray-500">Estado</p><p class="text-lg font-semibold ${u.estado === 'ACTIVO' ? 'text-green-600' : 'text-red-600'}">${u.estado}</p></div>
                </div>
            </div>
        `;
        document.getElementById('usuario-info-content').innerHTML = content;
        document.getElementById('ver-usuario-modal').classList.remove('hidden');
    } catch (error) {
        showToast('Error', 'No se pudo cargar el usuario', 'error');
    }
};

window.closeVerUsuarioModal = function() {
    document.getElementById('ver-usuario-modal').classList.add('hidden');
};

async function loadRoles() {
    try {
        const response = await fetch(`${window.API_BASE_URL}/roles`);
        if (response.ok) {
            roles = await response.json();
            const select = document.getElementById('usuario-rol');
            select.innerHTML = '<option value="">Seleccionar...</option>' +
                roles.map(r => `<option value="${r.id}">${r.nombre}</option>`).join('');
        }
    } catch (error) {
        console.error('Error loading roles:', error);
    }
}

window.showCreateUserModal = function() {
    currentUsuarioId = null;
    document.getElementById('usuario-modal-title').textContent = 'Crear Usuario';
    document.getElementById('usuario-form').reset();
    document.getElementById('usuario-id').value = '';
    document.getElementById('password-required').textContent = '*';
    document.getElementById('password-hint').textContent = 'Mínimo 6 caracteres';
    document.getElementById('usuario-password').required = true;
    document.getElementById('usuario-modal').classList.remove('hidden');
};

window.editUsuario = async function(id) {
    try {
        const response = await fetch(`${window.API_BASE_URL}/admin/usuarios/${id}`);
        if (!response.ok) throw new Error('Error');

        const usuario = await response.json();
        currentUsuarioId = id;
        document.getElementById('usuario-modal-title').textContent = 'Editar Usuario';
        document.getElementById('usuario-id').value = usuario.id;
        document.getElementById('usuario-username').value = usuario.username;
        document.getElementById('usuario-email').value = usuario.email;
        document.getElementById('usuario-rol').value = usuario.roleId;
        document.getElementById('usuario-estado').value = usuario.estado;
        document.getElementById('usuario-password').value = '';
        document.getElementById('usuario-password').required = false;
        document.getElementById('password-required').textContent = '';
        document.getElementById('password-hint').textContent = 'Dejar en blanco para no cambiar';
        document.getElementById('usuario-modal').classList.remove('hidden');
    } catch (error) {
        showToast('Error', 'No se pudo cargar el usuario', 'error');
    }
};

window.closeUsuarioModal = function() {
    document.getElementById('usuario-modal').classList.add('hidden');
    currentUsuarioId = null;
};

window.deleteUsuario = async function(id) {
    if (!confirm('¿Eliminar este usuario permanentemente?')) return;
    try {
        const response = await CsrfProtection.protectedFetch(`${window.API_BASE_URL}/admin/usuarios/${id}`, {
            method: 'DELETE'
        });
        if (response.ok) {
            showToast('Eliminado', 'Usuario eliminado', 'success');
            loadUsuarios();
            loadStats();
        } else {
            const data = await response.json();
            showToast('Error', data.error || 'No se pudo eliminar', 'error');
        }
    } catch (error) {
        showToast('Error', 'No se pudo eliminar', 'error');
    }
};

// ===== INICIALIZACIÓN =====
document.addEventListener('DOMContentLoaded', () => {
    // Verificar permisos de admin
    checkAdmin();

    // Cargar componentes
    Components.loadFooter();

    // Cargar datos iniciales
    loadStats();
    loadComentarios();
    loadRoles();

    // Setup form submit
    document.getElementById('usuario-form').addEventListener('submit', async (e) => {
        e.preventDefault();

        const payload = {
            username: document.getElementById('usuario-username').value,
            email: document.getElementById('usuario-email').value,
            roleId: parseInt(document.getElementById('usuario-rol').value),
            estado: document.getElementById('usuario-estado').value
        };

        const password = document.getElementById('usuario-password').value;
        if (password) payload.password = password;

        try {
            const url = currentUsuarioId
                ? `${window.API_BASE_URL}/admin/usuarios/${currentUsuarioId}`
                : `${window.API_BASE_URL}/admin/usuarios`;
            const method = currentUsuarioId ? 'PUT' : 'POST';

            const response = await CsrfProtection.protectedFetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                showToast('¡Éxito!', `Usuario ${currentUsuarioId ? 'actualizado' : 'creado'}`, 'success');
                closeUsuarioModal();
                loadUsuarios();
                loadStats();
            } else {
                const data = await response.json();
                showToast('Error', data.error || 'Error al guardar', 'error');
            }
        } catch (error) {
            showToast('Error', 'No se pudo guardar', 'error');
        }
    });
});

// Verificar que todas las funciones estén disponibles
console.log('🔍 Funciones disponibles:', {
    showTab: typeof window.showTab,
    loadComentarios: typeof window.loadComentarios,
    loadGraficas: typeof window.loadGraficas,
    loadUsuarios: typeof window.loadUsuarios,
    ocultarComentario: typeof window.ocultarComentario,
    eliminarComentario: typeof window.eliminarComentario
});
