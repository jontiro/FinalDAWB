package com.dawb.finaldawb.config;

import com.dawb.finaldawb.repository.*;
import com.dawb.finaldawb.security.CsrfTokenService;
import com.dawb.finaldawb.service.*;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Puente entre CDI (Weld) y HK2 (Jersey).
 * Permite que HK2 inyecte beans gestionados por CDI en los Resources de Jersey.
 * 
 * Este binder registra todos los Services y Repositories como factories
 * que obtienen las instancias del contenedor CDI.
 */
public class CdiHk2Binder extends AbstractBinder {

    @Override
    protected void configure() {
        // ===== SECURITY =====
        bindFactory(new CdiFactory<>(CsrfTokenService.class)).to(CsrfTokenService.class);

        // ===== SERVICES =====
        bindFactory(new CdiFactory<>(AuthService.class)).to(AuthService.class);
        bindFactory(new CdiFactory<>(UsuarioService.class)).to(UsuarioService.class);
        bindFactory(new CdiFactory<>(RecetaService.class)).to(RecetaService.class);
        bindFactory(new CdiFactory<>(LugarService.class)).to(LugarService.class);
        bindFactory(new CdiFactory<>(ComentarioService.class)).to(ComentarioService.class);
        bindFactory(new CdiFactory<>(TipoService.class)).to(TipoService.class);
        bindFactory(new CdiFactory<>(ObjetoService.class)).to(ObjetoService.class);
        bindFactory(new CdiFactory<>(RecomendacionService.class)).to(RecomendacionService.class);
        
        // ===== REPOSITORIES =====
        bindFactory(new CdiFactory<>(UsuarioRepository.class)).to(UsuarioRepository.class);
        bindFactory(new CdiFactory<>(RecetaRepository.class)).to(RecetaRepository.class);
        bindFactory(new CdiFactory<>(RecetaPasoRepository.class)).to(RecetaPasoRepository.class);
        bindFactory(new CdiFactory<>(TagRepository.class)).to(TagRepository.class);
        bindFactory(new CdiFactory<>(RecetaTagRepository.class)).to(RecetaTagRepository.class);
        bindFactory(new CdiFactory<>(LugarRepository.class)).to(LugarRepository.class);
        bindFactory(new CdiFactory<>(ComentarioRepository.class)).to(ComentarioRepository.class);
        bindFactory(new CdiFactory<>(TipoRepository.class)).to(TipoRepository.class);
        bindFactory(new CdiFactory<>(ObjetoRepository.class)).to(ObjetoRepository.class);
        bindFactory(new CdiFactory<>(RecomendacionRepository.class)).to(RecomendacionRepository.class);
        bindFactory(new CdiFactory<>(RoleRepository.class)).to(RoleRepository.class);
    }

    /**
     * Factory que obtiene instancias de beans CDI para ser usadas por HK2.
     * 
     * Implementa el patrón Factory de HK2 para proporcionar instancias
     * de beans gestionados por CDI (Weld).
     * 
     * @param <T> Tipo del bean a obtener del contenedor CDI
     */
    private static class CdiFactory<T> implements Factory<T> {
        private final Class<T> clazz;

        /**
         * Constructor de la factory.
         * 
         * @param clazz Clase del bean CDI a obtener
         */
        public CdiFactory(Class<T> clazz) {
            this.clazz = clazz;
        }

        /**
         * Proporciona una instancia del bean desde el contenedor CDI.
         * 
         * @return Instancia del bean gestionada por CDI
         */
        @Override
        public T provide() {
            try {
                // Método 1: Usar BeanManager (más robusto)
                BeanManager beanManager = CDI.current().getBeanManager();
                
                // Obtener el bean del tipo solicitado
                @SuppressWarnings("unchecked")
                Bean<T> bean = (Bean<T>) beanManager.getBeans(clazz).iterator().next();
                
                // Crear el contexto creacional
                CreationalContext<T> context = beanManager.createCreationalContext(bean);
                
                // Obtener la referencia al bean
                @SuppressWarnings("unchecked")
                T instance = (T) beanManager.getReference(bean, clazz, context);
                
                return instance;
            } catch (Exception e) {
                // Método 2: Fallback usando CDI.current().select()
                try {
                    return CDI.current().select(clazz).get();
                } catch (Exception ex) {
                    throw new RuntimeException("No se pudo obtener el bean " + clazz.getName() + " del contenedor CDI", ex);
                }
            }
        }

        /**
         * Dispone de la instancia del bean.
         * 
         * CDI gestiona el ciclo de vida de los beans, por lo que este método
         * no necesita hacer nada. Los beans @ApplicationScoped persisten durante
         * toda la vida de la aplicación.
         * 
         * @param instance Instancia a disponer (no usada)
         */
        @Override
        public void dispose(T instance) {
            // CDI gestiona el ciclo de vida automáticamente
            // No es necesario hacer nada aquí
        }
    }
}

