package com.dawb.finaldawb.service;

import com.dawb.finaldawb.domain.Comentario;
import com.dawb.finaldawb.domain.Objeto;
import com.dawb.finaldawb.domain.Usuario;
import com.dawb.finaldawb.repository.ComentarioRepository;
import com.dawb.finaldawb.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final ObjetoService objetoService;
    private final UsuarioRepository usuarioRepository;

    // Inyección por constructor
    @Inject
    public ComentarioService(ComentarioRepository comentarioRepository,
                             ObjetoService objetoService,
                             UsuarioRepository usuarioRepository) {
        this.comentarioRepository = comentarioRepository;
        this.objetoService = objetoService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene un comentario por ID.
     */
    public Optional<Comentario> findById(Long id) {
        return comentarioRepository.findById(id);
    }

    /**
     * Obtiene todos los comentarios de un autor específico.
     */
    public List<Comentario> findByAutorId(Long autorId) {
        return usuarioRepository.findById(autorId)
                .map(comentarioRepository::findByAutor)
                .orElseGet(List::of);
    }

    /**
     * Obtiene todos los comentarios asociados a un objeto por su ID genérico.
     * @param objetoId ID del Objeto (ej. ID de la Receta o ID del Lugar).
     */
    public List<Comentario> findAllByObjetoId(Long objetoId) { // <--- MÉTODO FALTANTE
        // 1. Asumimos que el objetoId es el ID del Objeto (entidad que es comentada).
        // 2. Buscamos el Objeto para pasárselo al repositorio.
        Optional<Objeto> objetoOpt = objetoService.findById(objetoId);

        return objetoOpt
                // Si el Objeto existe, busca los comentarios asociados a ese Objeto
                .map(comentarioRepository::findByObjeto)
                // Si no existe, devuelve una lista vacía
                .orElseGet(List::of);
    }

    /**
     * Obtiene todos los comentarios pendientes de moderación.
     * Requiere permisos de administrador o moderador en la capa REST.
     */
    public List<Comentario> findPendientesModeracion() {
        return comentarioRepository.findPendientesModeracion();
    }

    /**
     * Crea un nuevo comentario y lo clasifica según el tipo de entidad (Objeto).
     * @param comentario Objeto Comentario (sin autor, objeto o fechas).
     * @param autorId ID del usuario que publica.
     * @param tipoObjeto (ej. "Receta", "Lugar").
     * @return El Comentario persistido o Optional.empty() si el autor o el tipoObjeto no existen.
     */
    public Optional<Comentario> createComentario(Comentario comentario, Long autorId, Long objetoId, String tipoObjeto) {
        // 1. Verificar y obtener el Autor
        Optional<Usuario> autorOpt = usuarioRepository.findById(autorId);
        if (autorOpt.isEmpty()) {
            return Optional.empty(); // Autor no existe
        }

        // 2. Obtener el Objeto (clasificador polimórfico)
        Optional<Objeto> objetoOpt = objetoService.findByDescripcion(tipoObjeto);
        if (objetoOpt.isEmpty()) {
            return Optional.empty(); // Tipo Objeto no existe (Ej: "Receta" no está en la tabla 'objeto')
        }

        // 3. ASIGNACIÓN CLAVE: Aquí es donde se resuelve el ID real que apunta a la entidad comentada.
        // NOTA: Para un sistema polimórfico real, la entidad Comentario necesitaría la Columna
        // 'entidad_id' (Long) y 'entidad_tipo' (String) y NO la FK a Objeto.
        // Ya que usamos la FK a Objeto, asumimos que Objeto.id = objetoId.

        // Si tienes 1:1, aquí debe ir la verificación de la existencia de la Receta o Lugar.

        // 4. Asignar las entidades FK y asegurar que el ID sea null
        comentario.setAutor(autorOpt.get());
        comentario.setObjeto(objetoOpt.get()); // Asigna la entidad Objeto para el FK a la tabla 'objeto'
        comentario.setId(null);

        // 5. El campo 'moderado' se deja en su valor por defecto (false)
        return Optional.of(comentarioRepository.save(comentario));
    }

    /**
     * Marca un comentario como moderado/aprobado.
     * Requiere permisos de administrador.
     * @param comentarioId ID del comentario a moderar.
     * @param moderado Nuevo estado de moderación.
     * @return El Comentario actualizado.
     */
    public Optional<Comentario> updateModeracion(Long comentarioId, boolean moderado) {
        return comentarioRepository.findById(comentarioId)
                .map(comentario -> {
                    comentario.setModerado(moderado);
                    return comentarioRepository.save(comentario);
                });
    }

    /**
     * Elimina un comentario.
     * Requiere permisos de administrador O ser el autor.
     * NOTA: La verificación de permisos (ser autor o admin) debe hacerse en la capa de REST/seguridad.
     */
    public boolean deleteComentario(Long comentarioId) {
        Optional<Comentario> comentarioOpt = comentarioRepository.findById(comentarioId);
        if (comentarioOpt.isPresent()) {
            comentarioRepository.delete(comentarioOpt.get());
            return true;
        }
        return false;
    }
}