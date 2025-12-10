package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Repository;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.CompraProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraProductoRepository extends JpaRepository<CompraProducto, Long> {

    @Query("SELECT cp FROM CompraProducto cp WHERE cp.compra.id_Compra = :idCompra")
    List<CompraProducto> findByCompraId(@Param("idCompra") Long idCompra);
}
