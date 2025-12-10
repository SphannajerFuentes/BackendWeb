package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Repository;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM promocion_producto WHERE id_producto = :productoId", nativeQuery = true)
    void deleteRelacionProducto(@Param("productoId") Long productoId);
}
