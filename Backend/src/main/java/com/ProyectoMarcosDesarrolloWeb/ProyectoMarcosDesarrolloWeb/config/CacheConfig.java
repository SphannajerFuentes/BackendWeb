package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        // Cache por defecto
        CaffeineCache defaultCache = new CaffeineCache("defaultCache",
                Caffeine.newBuilder()
                        .expireAfterWrite(2, TimeUnit.MINUTES)
                        .maximumSize(100)
                        .build());

        // Cache para códigos de recuperación
        CaffeineCache codigosCache = new CaffeineCache("codigos",
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES) // Expira en 5 minutos
                        .maximumSize(1000)
                        .build());

        cacheManager.setCaches(List.of(defaultCache, codigosCache));
        return cacheManager;
    }
}
