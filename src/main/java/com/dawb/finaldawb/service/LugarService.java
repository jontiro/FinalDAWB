package com.dawb.finaldawb.service;

import com.dawb.finaldawb.domain.Lugar;
import com.dawb.finaldawb.domain.Usuario;
import com.dawb.finaldawb.repository.LugarRepository;
import com.dawb.finaldawb.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class LugarService {

    private final LugarRepository lugarRepository;
    private final UsuarioRepository usuarioRepository;

    // Inyección por constructor
    @Inject
    public LugarService(LugarRepository lugarRepository, UsuarioRepository usuarioRepository) {
        this.lugarRepository = lugarRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene todos los lugares registrados.
     */
    public List<Lugar> findAll() {
        return lugarRepository.findAll();
    }

    /**
     * Obtiene un lugar por ID.
     */
    public Optional<Lugar> findById(Long id) {
        return lugarRepository.findById(id);
    }

    /**
     * Obtiene todos los lugares creados por un usuario específico.
     * @param autorId ID del usuario creador.
     */
    public List<Lugar> findAllByAutorId(Long autorId) {
        return usuarioRepository.findById(autorId)
                .map(lugarRepository::findByAutor)
                .orElseGet(List::of); // Devuelve una lista vacía si el usuario no existe
    }

    /**
     * Crea un nuevo lugar.
     * Requiere el ID del autor para enlazar el objeto Usuario.
     * @param lugar Objeto Lugar (sin ID ni autor asignado).
     * @param autorId ID del usuario creador.
     * @return El lugar persistido o Optional.empty() si el autor no existe.
     */
    public Optional<Lugar> createLugar(Lugar lugar, Long autorId) {
        return usuarioRepository.findById(autorId)
                .map(autor -> {
                    lugar.setAutor(autor);
                    // El campo 'id' debe ser null para que save() haga persist (INSERT)
                    lugar.setId(null);
                    return lugarRepository.save(lugar);
                });
    }

    /**
     * Actualiza un lugar existente.
     * @param lugar Lugar con los datos actualizados (debe tener ID).
     * @param autorId ID del autor, se utiliza para verificar que el usuario tenga permisos.
     * @return El lugar actualizado o Optional.empty() si no existe o el autor no coincide.
     */
    public Optional<Lugar> updateLugar(Lugar lugar, Long autorId) {
        // 1. Verificar si el lugar existe
        return lugarRepository.findById(lugar.getId())
                .filter(existingLugar -> existingLugar.getAutor().getId().equals(autorId)) // 2. Verificar que el autor es el mismo
                .map(existingLugar -> {
                    // 3. Aplicar solo los campos editables
                    existingLugar.setNombre(lugar.getNombre());
                    existingLugar.setDireccion(lugar.getDireccion());
                    existingLugar.setCiudad(lugar.getCiudad());
                    existingLugar.setPais(lugar.getPais());
                    // 4. Guardar (merge)
                    return lugarRepository.save(existingLugar);
                });
    }

    /**
     * Elimina un lugar por ID, verificando que el autor sea el correcto.
     * @param id ID del lugar a eliminar.
     * @param autorId ID del usuario que intenta eliminarlo.
     * @return true si fue eliminado.
     */
    public boolean deleteLugar(Long id, Long autorId) {
        return lugarRepository.findById(id)
                .filter(lugar -> lugar.getAutor().getId().equals(autorId))
                .map(lugar -> {
                    lugarRepository.delete(lugar);
                    return true;
                })
                .orElse(false);
    }
}