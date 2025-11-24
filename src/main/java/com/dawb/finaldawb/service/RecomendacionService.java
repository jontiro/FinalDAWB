package com.dawb.finaldawb.service;

import com.dawb.finaldawb.domain.Recomendacion;
import com.dawb.finaldawb.domain.Tipo;
import com.dawb.finaldawb.domain.Usuario;
import com.dawb.finaldawb.repository.RecomendacionRepository;
import com.dawb.finaldawb.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class RecomendacionService {

    private RecomendacionRepository recomendacionRepository;
    private UsuarioRepository usuarioRepository;
    private TipoService tipoService;

    // Constructor vacío requerido por CDI
    protected RecomendacionService() {
    }

    // Inyección por constructor
    @Inject
    public RecomendacionService(RecomendacionRepository recomendacionRepository,
                                UsuarioRepository usuarioRepository,
                                TipoService tipoService) {
        this.recomendacionRepository = recomendacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.tipoService = tipoService;
    }

    /**
     * Obtiene una recomendación por ID.
     */
    public Optional<Recomendacion> findById(Long id) {
        return recomendacionRepository.findById(id);
    }

    /**
     * Obtiene todas las recomendaciones, ordenadas por fecha.
     */
    public List<Recomendacion> findAll() {
        return recomendacionRepository.findAll();
    }

    /**
     * Crea una nueva recomendación.
     * Realiza validación de FK (autor y tipo).
     * @param recomendacion Objeto Recomendacion (sin FK resueltas).
     * @param autorId ID del usuario creador.
     * @param tipoId ID del tipo de recomendación (Articulo, Video, etc.).
     * @return La Recomendacion persistida o Optional.empty() si el autor o tipo no existen.
     */
    public Optional<Recomendacion> createRecomendacion(Recomendacion recomendacion, Long autorId, Long tipoId) {
        // 1. Verificar y obtener el Autor
        Optional<Usuario> autorOpt = usuarioRepository.findById(autorId);
        if (autorOpt.isEmpty()) {
            return Optional.empty(); // Autor no existe
        }

        // 2. Verificar y obtener el Tipo de Recomendación
        Optional<Tipo> tipoOpt = tipoService.findById(tipoId);
        if (tipoOpt.isEmpty()) {
            return Optional.empty(); // Tipo no existe
        }

        // 3. Asignar las entidades FK y asegurar que el ID sea null para persistir
        recomendacion.setAutor(autorOpt.get());
        recomendacion.setTipo(tipoOpt.get());
        recomendacion.setId(null);

        // 4. Guardar y devolver
        return Optional.of(recomendacionRepository.save(recomendacion));
    }

    /**
     * Actualiza una recomendación existente.
     * @param recomendacion Con los datos actualizados (debe tener ID).
     * @param currentUserId ID del usuario que intenta actualizar (Control de Acceso).
     * @return La Recomendacion actualizada o Optional.empty() si no existe, o el usuario no es el autor.
     */
    public Optional<Recomendacion> updateRecomendacion(Recomendacion recomendacion, Long currentUserId) {
        // 1. Buscar la recomendación existente
        return recomendacionRepository.findById(recomendacion.getId())
                // 2. Verificar Control de Acceso: El usuario actual debe ser el autor
                .filter(existing -> existing.getAutor().getId().equals(currentUserId))
                .map(existing -> {
                    // 3. Aplicar solo los campos editables
                    existing.setTitulo(recomendacion.getTitulo());
                    existing.setCuerpo(recomendacion.getCuerpo());

                    // 4. Si el tipoId viene en la recomendacion, se puede actualizar el tipo.
                    // Aquí asumimos que el objeto 'recomendacion' de entrada tiene el tipo existente.
                    // Si el tipo debe ser actualizable, se requiere lógica adicional para verificar el nuevo tipoId.

                    // 5. Guardar (merge)
                    return recomendacionRepository.save(existing);
                });
    }

    /**
     * Elimina una recomendación, verificando que el autor sea el correcto.
     */
    public boolean deleteRecomendacion(Long id, Long currentUserId) {
        return recomendacionRepository.findById(id)
                // 1. Verificar Control de Acceso
                .filter(recomendacion -> recomendacion.getAutor().getId().equals(currentUserId))
                .map(recomendacion -> {
                    recomendacionRepository.delete(recomendacion);
                    return true;
                })
                .orElse(false);
    }

    // --- Búsquedas Útiles ---

    public List<Recomendacion> findByAutorId(Long autorId) {
        return usuarioRepository.findById(autorId)
                .map(recomendacionRepository::findByAutor)
                .orElseGet(List::of);
    }

    public List<Recomendacion> findByTipoId(Long tipoId) {
        return tipoService.findById(tipoId)
                .map(recomendacionRepository::findByTipo)
                .orElseGet(List::of);
    }
}