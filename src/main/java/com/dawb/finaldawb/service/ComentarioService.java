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
    public Optional<Comentario> createComentario(Comentario comentario, Long autorId, String tipoObjeto) {
        // 1. Verificar y obtener el Autor
        Optional<Usuario> autorOpt = usuarioRepository.findById(autorId);
        if (autorOpt.isEmpty()) {
            return Optional.empty(); // Autor no existe
        }

        // 2. Verificar y obtener el Objeto (clasificador polimórfico)
        Optional<Objeto> objetoOpt = objetoService.findByDescripcion(tipoObjeto);
        if (objetoOpt.isEmpty()) {
            // Este es un error de dato, el tipo Objeto debe existir ("Receta" o "Lugar")
            return Optional.empty();
        }

        // 3. Asignar las entidades FK y asegurar que el ID sea null
        comentario.setAutor(autorOpt.get());
        comentario.setObjeto(objetoOpt.get());
        comentario.setId(null);

        // 4. El campo 'moderado' se deja en su valor por defecto (false)
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