package com.dawb.finaldawb.service;

import com.dawb.finaldawb.domain.Comentario;
import com.dawb.finaldawb.domain.Objeto;
import com.dawb.finaldawb.domain.Receta;
import com.dawb.finaldawb.domain.Usuario;
import com.dawb.finaldawb.repository.ComentarioRepository;
import com.dawb.finaldawb.repository.RecetaRepository;
import com.dawb.finaldawb.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ComentarioService {

    @Inject
    private EntityManager em;

    private ComentarioRepository comentarioRepository;
    private ObjetoService objetoService;
    private UsuarioRepository usuarioRepository;
    private RecetaRepository recetaRepository;

    // Constructor vacío requerido por CDI
    protected ComentarioService() {
    }

    // Inyección por constructor
    @Inject
    public ComentarioService(ComentarioRepository comentarioRepository,
                             ObjetoService objetoService,
                             UsuarioRepository usuarioRepository,
                             RecetaRepository recetaRepository,
                             EntityManager em) {
        this.comentarioRepository = comentarioRepository;
        this.objetoService = objetoService;
        this.usuarioRepository = usuarioRepository;
        this.recetaRepository = recetaRepository;
        this.em = em;
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
     * Obtiene todos los comentarios de una receta específica.
     * @param recetaId ID de la receta.
     * @return Lista de comentarios de esa receta.
     */
    public List<Comentario> findByRecetaId(Long recetaId) {
        // 1. Obtener el objeto tipo "Receta"
        Optional<Objeto> objetoRecetaOpt = objetoService.findByDescripcion("Receta");

        if (objetoRecetaOpt.isEmpty()) {
            return List.of(); // No existe el tipo "Receta" en la tabla objeto
        }

        // 2. Buscar comentarios por entidadId (recetaId) y objeto (tipo Receta)
        return comentarioRepository.findByEntidadIdAndObjeto(recetaId, objetoRecetaOpt.get());
    }

    /**
     * Obtiene todos los comentarios asociados a un objeto por su ID genérico.
     * @deprecated Usar findByRecetaId o métodos específicos por entidad
     */
    @Deprecated
    public List<Comentario> findAllByObjetoId(Long objetoId) {
        Optional<Objeto> objetoOpt = objetoService.findById(objetoId);
        return objetoOpt
                .map(comentarioRepository::findByObjeto)
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
     * Crea un nuevo comentario sobre una receta.
     * @param contenido Texto del comentario.
     * @param autorId ID del usuario que publica.
     * @param recetaId ID de la receta comentada.
     * @return El Comentario persistido.
     * @throws IllegalArgumentException si el usuario o la receta no existen.
     */
    public Comentario createComentarioReceta(String contenido, Long autorId, Long recetaId) {
        // Iniciar transacción manual
        em.getTransaction().begin();
        try {
            // 1. Verificar y obtener el Autor
            Usuario autor = usuarioRepository.findById(autorId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + autorId));

            // 2. Verificar y obtener la Receta
            Receta receta = recetaRepository.findById(recetaId)
                    .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada con id: " + recetaId));

            // 3. Obtener el Objeto tipo "Receta"
            Objeto objetoReceta = objetoService.findByDescripcion("Receta")
                    .orElseThrow(() -> new IllegalStateException("Tipo 'Receta' no existe en la tabla objeto"));

            // 4. Crear el comentario
            Comentario comentario = new Comentario();
            comentario.setContenido(contenido);
            comentario.setAutor(autor);
            comentario.setObjeto(objetoReceta);
            comentario.setEntidadId(recetaId);
            comentario.setModerado(false);

            // 5. Persistir
            Comentario savedComentario = comentarioRepository.save(comentario);

            // 6. Flush para obtener el ID generado
            em.flush();

            // 7. Commit de la transacción
            em.getTransaction().commit();

            return savedComentario;
        } catch (Exception e) {
            // Rollback en caso de error
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Crea un nuevo comentario (método genérico, compatible con el código anterior).
     * @deprecated Usar createComentarioReceta() para mayor claridad
     */
    @Deprecated
    public Optional<Comentario> createComentario(Comentario comentario, Long autorId, Long recetaId, String tipoObjeto) {
        try {
            // 1. Verificar y obtener el Autor
            Usuario autor = usuarioRepository.findById(autorId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // 2. Verificar que la receta existe (si es tipo Receta)
            if ("Receta".equals(tipoObjeto)) {
                recetaRepository.findById(recetaId)
                        .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada"));
            }

            // 3. Obtener el Objeto (clasificador polimórfico)
            Objeto objeto = objetoService.findByDescripcion(tipoObjeto)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo Objeto no existe: " + tipoObjeto));

            // 4. Asignar las propiedades
            comentario.setAutor(autor);
            comentario.setObjeto(objeto);
            comentario.setEntidadId(recetaId); // ID de la entidad específica (receta, lugar, etc.)
            comentario.setId(null);

            // 5. Persistir
            return Optional.of(comentarioRepository.save(comentario));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Marca un comentario como moderado/aprobado.
     * Requiere permisos de administrador.
     * @param comentarioId ID del comentario a moderar.
     * @param moderado Nuevo estado de moderación.
     * @return El Comentario actualizado.
     */
    public Optional<Comentario> updateModeracion(Long comentarioId, boolean moderado) {
        em.getTransaction().begin();
        try {
            Optional<Comentario> result = comentarioRepository.findById(comentarioId)
                    .map(comentario -> {
                        comentario.setModerado(moderado);
                        return comentarioRepository.save(comentario);
                    });
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Elimina un comentario.
     * Requiere permisos de administrador O ser el autor.
     * NOTA: La verificación de permisos (ser autor o admin) debe hacerse en la capa de REST/seguridad.
     */
    public boolean deleteComentario(Long comentarioId) {
        em.getTransaction().begin();
        try {
            Optional<Comentario> comentarioOpt = comentarioRepository.findById(comentarioId);
            if (comentarioOpt.isPresent()) {
                comentarioRepository.delete(comentarioOpt.get());
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }
}