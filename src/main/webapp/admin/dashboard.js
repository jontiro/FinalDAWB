// API Base URL
const API_BASE = '/FinalDAWB-1.0-SNAPSHOT/api';

// Estado global
let currentData = {
    usuarios: [],
    recetas: [],
    comentarios: [],
    lugares: [],
    tipos: []
};

// Verificar autenticación al cargar
document.addEventListener('DOMContentLoaded', () => {
    if (!isAuthenticated()) {
        alert('Acceso denegado. Solo administradores pueden acceder a esta página.');
        window.location.href = '../home.html';
        return;
    }

    initDashboard();
});

// Inicializar dashboard
async function initDashboard() {
    setupTabs();
    await loadStats();
    await loadUsuarios();
    setupSearchFilters();
}

// Configurar tabs
function setupTabs() {
    const tabButtons = document.querySelectorAll('.tab-button');
    
    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const tabName = button.dataset.tab;
            switchTab(tabName);
        });
    });
}

// Cambiar de tab
async function switchTab(tabName) {
    // Actualizar botones
    document.querySelectorAll('.tab-button').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');

    // Actualizar contenido
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });
    document.getElementById(`tab-${tabName}`).classList.add('active');

    // Cargar datos si es necesario
    switch(tabName) {
        case 'usuarios':
            if (currentData.usuarios.length === 0) await loadUsuarios();
            break;
        case 'recetas':
            if (currentData.recetas.length === 0) await loadRecetas();
            break;
        case 'comentarios':
            if (currentData.comentarios.length === 0) await loadComentarios();
            break;
        case 'lugares':
            if (currentData.lugares.length === 0) await loadLugares();
            break;
        case 'tipos':
            if (currentData.tipos.length === 0) await loadTipos();
            break;
    }
}

// Configurar filtros de búsqueda
function setupSearchFilters() {
    document.getElementById('search-usuarios').addEventListener('input', (e) => {
        filterTable('usuarios', e.target.value);
    });
    document.getElementById('search-recetas').addEventListener('input', (e) => {
        filterTable('recetas', e.target.value);
    });
    document.getElementById('search-comentarios').addEventListener('input', (e) => {
        filterTable('comentarios', e.target.value);
    });
    document.getElementById('search-lugares').addEventListener('input', (e) => {
        filterTable('lugares', e.target.value);
    });
}

// Filtrar tabla
function filterTable(type, query) {
    const rows = document.querySelectorAll(`#${type}-container tr:not(:first-child)`);
    query = query.toLowerCase();

    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(query) ? '' : 'none';
    });
}

// ========== ESTADÍSTICAS ==========

async function loadStats() {
    try {
        const [usuarios, recetas, comentarios, lugares] = await Promise.all([
            fetchAPI('/admin/usuarios'),
            fetchAPI('/recetas'),
            fetchAPI('/admin/comentarios/todos'),
            fetchAPI('/lugares')
        ]);

        document.getElementById('stat-usuarios').textContent = usuarios.length || 0;
        document.getElementById('stat-recetas').textContent = recetas.length || 0;
        document.getElementById('stat-comentarios').textContent = comentarios.length || 0;
        document.getElementById('stat-lugares').textContent = lugares.length || 0;
    } catch (error) {
        console.error('Error cargando estadísticas:', error);
    }
}

// ========== USUARIOS ==========

async function loadUsuarios() {
    try {
        const usuarios = await fetchAPI('/admin/usuarios');
        currentData.usuarios = usuarios;
        renderUsuarios(usuarios);
    } catch (error) {
        showError('usuarios-container', 'Error cargando usuarios');
    }
}

function renderUsuarios(usuarios) {
    const container = document.getElementById('usuarios-container');
    
    if (usuarios.length === 0) {
        container.innerHTML = '<div class="empty-state">No hay usuarios registrados</div>';
        return;
    }

    const table = `
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Rol</th>
                    <th>Estado</th>
                    <th>Registro</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                ${usuarios.map(u => `
                    <tr>
                        <td>${u.id}</td>
                        <td>${u.username}</td>
                        <td>${u.email}</td>
                        <td><span class="badge ${u.roleNombre === 'ADMIN' ? 'badge-warning' : 'badge-success'}">${u.roleNombre}</span></td>
                        <td><span class="badge ${u.estado === 'ACTIVO' ? 'badge-success' : 'badge-danger'}">${u.estado}</span></td>
                        <td>${formatDate(u.fechaRegistro)}</td>
                        <td class="action-buttons">
                            <button class="btn btn-primary btn-small" onclick="editUsuario(${u.id})">✏️ Editar</button>
                            <button class="btn btn-danger btn-small" onclick="deleteUsuario(${u.id})">🗑️ Eliminar</button>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    
    container.innerHTML = table;
}

function showCreateUserModal() {
    document.getElementById('modal-usuario-title').textContent = 'Nuevo Usuario';
    document.getElementById('form-usuario').reset();
    document.getElementById('usuario-id').value = '';
    document.getElementById('usuario-password').required = true;
    openModal('modal-usuario');
}

async function editUsuario(id) {
    const usuario = currentData.usuarios.find(u => u.id === id);
    if (!usuario) return;

    document.getElementById('modal-usuario-title').textContent = 'Editar Usuario';
    document.getElementById('usuario-id').value = usuario.id;
    document.getElementById('usuario-username').value = usuario.username;
    document.getElementById('usuario-email').value = usuario.email;
    document.getElementById('usuario-password').value = '';
    document.getElementById('usuario-password').required = false;
    document.getElementById('usuario-role').value = usuario.roleId;
    document.getElementById('usuario-estado').value = usuario.estado;
    
    openModal('modal-usuario');
}

async function saveUsuario(event) {
    event.preventDefault();
    
    const id = document.getElementById('usuario-id').value;
    const data = {
        username: document.getElementById('usuario-username').value,
        email: document.getElementById('usuario-email').value,
        password: document.getElementById('usuario-password').value,
        roleId: parseInt(document.getElementById('usuario-role').value),
        estado: document.getElementById('usuario-estado').value
    };

    try {
        if (id) {
            // Actualizar
            await fetchAPI(`/admin/usuarios/${id}`, 'PUT', data);
            showNotification('Usuario actualizado correctamente', 'success');
        } else {
            // Crear
            await fetchAPI('/admin/usuarios', 'POST', data);
            showNotification('Usuario creado correctamente', 'success');
        }
        
        closeModal('modal-usuario');
        await loadUsuarios();
        await loadStats();
    } catch (error) {
        showNotification('Error: ' + error.message, 'error');
    }
}

async function deleteUsuario(id) {
    if (!confirm('¿Estás seguro de eliminar este usuario?')) return;

    try {
        await fetchAPI(`/admin/usuarios/${id}`, 'DELETE');
        showNotification('Usuario eliminado correctamente', 'success');
        await loadUsuarios();
        await loadStats();
    } catch (error) {
        showNotification('Error: ' + error.message, 'error');
    }
}

// ========== RECETAS ==========

async function loadRecetas() {
    try {
        const recetas = await fetchAPI('/recetas');
        currentData.recetas = recetas;
        renderRecetas(recetas);
    } catch (error) {
        showError('recetas-container', 'Error cargando recetas');
    }
}

function renderRecetas(recetas) {
    const container = document.getElementById('recetas-container');
    
    if (recetas.length === 0) {
        container.innerHTML = '<div class="empty-state">No hay recetas publicadas</div>';
        return;
    }

    const table = `
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Título</th>
                    <th>Creador</th>
                    <th>Tiempo (min)</th>
                    <th>Privacidad</th>
                    <th>Fecha</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                ${recetas.map(r => `
                    <tr>
                        <td>${r.id}</td>
                        <td>${r.titulo}</td>
                        <td>${r.creadorUsername || 'N/A'}</td>
                        <td>${r.tiempoPreparacion}</td>
                        <td><span class="badge ${r.privacidad ? 'badge-warning' : 'badge-success'}">${r.privacidad ? 'Privada' : 'Pública'}</span></td>
                        <td>${formatDate(r.fechaCreacion)}</td>
                        <td class="action-buttons">
                            <button class="btn btn-danger btn-small" onclick="deleteReceta(${r.id})">🗑️ Eliminar</button>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    
    container.innerHTML = table;
}

async function deleteReceta(id) {
    if (!confirm('¿Estás seguro de eliminar esta receta?')) return;

    try {
        await fetchAPI(`/recetas/${id}`, 'DELETE');
        showNotification('Receta eliminada correctamente', 'success');
        await loadRecetas();
        await loadStats();
    } catch (error) {
        showNotification('Error: ' + error.message, 'error');
    }
}

// ========== COMENTARIOS ==========

async function loadComentarios() {
    try {
        const comentarios = await fetchAPI('/admin/comentarios/todos');
        currentData.comentarios = comentarios;
        renderComentarios(comentarios);
    } catch (error) {
        showError('comentarios-container', 'Error cargando comentarios');
    }
}

async function loadPendingComments() {
    try {
        const comentarios = await fetchAPI('/admin/moderacion/pendientes');
        currentData.comentarios = comentarios;
        renderComentarios(comentarios);
        showNotification(`${comentarios.length} comentarios pendientes de moderación`, 'info');
    } catch (error) {
        showNotification('Error cargando comentarios pendientes', 'error');
    }
}

function renderComentarios(comentarios) {
    const container = document.getElementById('comentarios-container');
    
    if (comentarios.length === 0) {
        container.innerHTML = '<div class="empty-state">No hay comentarios</div>';
        return;
    }

    const table = `
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Texto</th>
                    <th>Autor</th>
                    <th>Receta ID</th>
                    <th>Estado</th>
                    <th>Fecha</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                ${comentarios.map(c => `
                    <tr>
                        <td>${c.id}</td>
                        <td>${c.texto.substring(0, 50)}${c.texto.length > 50 ? '...' : ''}</td>
                        <td>${c.autorUsername}</td>
                        <td>${c.recetaId || 'N/A'}</td>
                        <td><span class="badge ${c.moderado ? 'badge-success' : 'badge-warning'}">${c.moderado ? 'Aprobado' : 'Pendiente'}</span></td>
                        <td>${formatDate(c.fechaCreacion)}</td>
                        <td class="action-buttons">
                            ${!c.moderado ? `<button class="btn btn-success btn-small" onclick="moderateComment(${c.id}, true)">✓ Aprobar</button>` : ''}
                            <button class="btn btn-danger btn-small" onclick="deleteComentario(${c.id})">🗑️ Eliminar</button>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    
    container.innerHTML = table;
}

async function moderateComment(id, estado) {
    try {
        await fetchAPI(`/admin/moderacion/${id}/estado?moderado=${estado}`, 'POST');
        showNotification('Comentario moderado correctamente', 'success');
        await loadComentarios();
    } catch (error) {
        showNotification('Error: ' + error.message, 'error');
    }
}

async function deleteComentario(id) {
    if (!confirm('¿Estás seguro de eliminar este comentario?')) return;

    try {
        await fetchAPI(`/comentarios/${id}`, 'DELETE');
        showNotification('Comentario eliminado correctamente', 'success');
        await loadComentarios();
        await loadStats();
    } catch (error) {
        showNotification('Error: ' + error.message, 'error');
    }
}

// ========== LUGARES ==========

async function loadLugares() {
    try {
        const lugares = await fetchAPI('/lugares');
        currentData.lugares = lugares;
        renderLugares(lugares);
    } catch (error) {
        showError('lugares-container', 'Error cargando lugares');
    }
}

function renderLugares(lugares) {
    const container = document.getElementById('lugares-container');
    
    if (lugares.length === 0) {
        container.innerHTML = '<div class="empty-state">No hay lugares registrados</div>';
        return;
    }

    const table = `
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Ciudad</th>
                    <th>País</th>
                    <th>Autor</th>
                    <th>Fecha</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                ${lugares.map(l => `
                    <tr>
                        <td>${l.id}</td>
                        <td>${l.nombre}</td>
                        <td>${l.ciudad || 'N/A'}</td>
                        <td>${l.pais || 'N/A'}</td>
                        <td>${l.autorUsername || 'N/A'}</td>
                        <td>${formatDate(l.fechaCreacion)}</td>
                        <td class="action-buttons">
                            <button class="btn btn-danger btn-small" onclick="deleteLugar(${l.id})">🗑️ Eliminar</button>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    
    container.innerHTML = table;
}

async function deleteLugar(id) {
    if (!confirm('¿Estás seguro de eliminar este lugar?')) return;

    try {
        await fetchAPI(`/lugares/${id}`, 'DELETE');
        showNotification('Lugar eliminado correctamente', 'success');
        await loadLugares();
        await loadStats();
    } catch (error) {
        showNotification('Error: ' + error.message, 'error');
    }
}

// ========== TIPOS ==========

async function loadTipos() {
    try {
        const tipos = await fetchAPI('/admin/tipos');
        currentData.tipos = tipos;
        renderTipos(tipos);
    } catch (error) {
        showError('tipos-container', 'Error cargando tipos');
    }
}

function renderTipos(tipos) {
    const container = document.getElementById('tipos-container');
    
    if (tipos.length === 0) {
        container.innerHTML = '<div class="empty-state">No hay tipos definidos</div>';
        return;
    }

    const table = `
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Descripción</th>
                    <th>Fecha Creación</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                ${tipos.map(t => `
                    <tr>
                        <td>${t.id}</td>
                        <td>${t.descripcion}</td>
                        <td>${formatDate(t.creadoEn)}</td>
                        <td class="action-buttons">
                            <button class="btn btn-danger btn-small" onclick="deleteTipo(${t.id})">🗑️ Eliminar</button>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    
    container.innerHTML = table;
}

function showCreateTipoModal() {
    document.getElementById('form-tipo').reset();
    openModal('modal-tipo');
}

async function saveTipo(event) {
    event.preventDefault();
    
    const descripcion = document.getElementById('tipo-descripcion').value;

    try {
        await fetchAPI(`/admin/tipos/${descripcion}`, 'POST');
        showNotification('Tipo creado correctamente', 'success');
        closeModal('modal-tipo');
        await loadTipos();
    } catch (error) {
        showNotification('Error: ' + error.message, 'error');
    }
}

async function deleteTipo(id) {
    if (!confirm('¿Estás seguro de eliminar este tipo?')) return;

    try {
        await fetchAPI(`/admin/tipos/${id}`, 'DELETE');
        showNotification('Tipo eliminado correctamente', 'success');
        await loadTipos();
    } catch (error) {
        showNotification('Error: ' + error.message, 'error');
    }
}

// ========== UTILIDADES ==========

async function fetchAPI(endpoint, method = 'GET', body = null) {
    const token = localStorage.getItem('token');
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    };

    if (body && method !== 'GET') {
        options.body = JSON.stringify(body);
    }

    const response = await fetch(API_BASE + endpoint, options);
    
    if (!response.ok) {
        if (response.status === 401) {
            logout();
            throw new Error('Sesión expirada');
        }
        throw new Error('Error en la solicitud');
    }

    if (method === 'DELETE') {
        return true;
    }

    return await response.json();
}

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('es-MX', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function openModal(modalId) {
    document.getElementById(modalId).classList.add('active');
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
}

function showError(containerId, message) {
    const container = document.getElementById(containerId);
    container.innerHTML = `<div class="empty-state">⚠️ ${message}</div>`;
}

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 25px;
        background: ${type === 'success' ? '#48bb78' : type === 'error' ? '#f56565' : '#667eea'};
        color: white;
        border-radius: 5px;
        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        z-index: 9999;
        animation: slideIn 0.3s ease;
    `;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '../home.html';
}

function isAuthenticated() {
    const user = localStorage.getItem('user');
    if (!user) return false;

    try {
        const userData = JSON.parse(user);
        return userData && userData.role === 'ADMIN';
    } catch (e) {
        return false;
    }
}

// Agregar estilos para animaciones
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(400px);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(400px);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);

