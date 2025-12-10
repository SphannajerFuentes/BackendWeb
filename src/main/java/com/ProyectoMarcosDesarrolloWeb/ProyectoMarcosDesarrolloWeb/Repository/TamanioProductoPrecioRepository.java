package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Repository;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.TamanioProductoPrecio;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.TamanioProductoPrecioId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // Importante para devolver una lista
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TamanioProductoPrecioRepository
        extends JpaRepository<TamanioProductoPrecio, TamanioProductoPrecioId> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Tamanio_Producto_Precio WHERE id_Producto = :id", nativeQuery = true)
    void deleteByProductoId(Long id);

    List<TamanioProductoPrecio> findByProductoIdProducto(Long idProducto);

}
