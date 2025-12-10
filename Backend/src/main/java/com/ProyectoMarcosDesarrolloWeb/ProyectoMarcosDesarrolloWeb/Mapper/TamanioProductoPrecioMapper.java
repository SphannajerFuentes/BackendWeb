// Archivo: TamanioProductoPrecioMapper.java
package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Mapper;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.Producto;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.TamanioProductoPrecio;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.TamanioPrecioDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.Tamanio_Pizza;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.TamanioProductoPrecioId;
import org.springframework.stereotype.Component;

@Component
public class TamanioProductoPrecioMapper {

    // Conversión de Entidad (DB) a DTO (Web/JSON)
    public TamanioPrecioDTO toDTO(TamanioProductoPrecio tpp) {
        if (tpp == null) {
            return null;
        }
        // Usar el constructor correcto: (Integer idTamanio, Long idProducto, String nombreTamanio, BigDecimal precio)
        return new TamanioPrecioDTO(
                tpp.getTamanioPizza().getId_Tamanio(),
                tpp.getProducto().getIdProducto(),
                tpp.getTamanioPizza().getNombre(),
                tpp.getPrecio()
        );
    }

    public TamanioProductoPrecio toEntity(TamanioPrecioDTO dto, Tamanio_Pizza tamanio_Pizza, Producto producto) {
        if (dto == null || tamanio_Pizza == null || producto == null) {
            return null;
        }
        TamanioProductoPrecio entidad = new TamanioProductoPrecio();
        TamanioProductoPrecioId id = new TamanioProductoPrecioId(
                tamanio_Pizza.getId_Tamanio(),
                producto.getIdProducto()
        );
        entidad.setId(id);
        entidad.setTamanioPizza(tamanio_Pizza);
        entidad.setProducto(producto);
        entidad.setPrecio(dto.getPrecio());
        return entidad;
    }
}
