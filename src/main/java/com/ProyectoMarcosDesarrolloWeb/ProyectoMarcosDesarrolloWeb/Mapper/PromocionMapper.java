package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Mapper;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.Promocion;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.PromocionesDTO;
import java.util.Base64;
import static org.hibernate.query.results.Builders.entity;
import org.springframework.stereotype.Component;

@Component
public class PromocionMapper {

    public PromocionesDTO toDTO(Promocion promocion) {
        PromocionesDTO dto = new PromocionesDTO();
        dto.setIdPromocion(promocion.getIdPromocion());
        dto.setNombre(promocion.getNombre());
        dto.setDescripcion(promocion.getDescripcion());
        dto.setDescuentoPorcentaje(promocion.getDescuentoPorcentaje());
        dto.setFechaInicio(promocion.getFechaInicio());
        dto.setFechaFin(promocion.getFechaFin());
        if (promocion.getImagenReferencia()!= null) {
            // Convertir byte[] a Base64 para enviar al frontend
            String base64 = Base64.getEncoder().encodeToString(promocion.getImagenReferencia());
            dto.setImagenReferencia(base64);
        }
        return dto;
    }

    public Promocion toEntity(PromocionesDTO dto) {
        Promocion promocion = new Promocion();
        promocion.setIdPromocion(dto.getIdPromocion());
        promocion.setNombre(dto.getNombre());
        promocion.setDescripcion(dto.getDescripcion());
        promocion.setDescuentoPorcentaje(dto.getDescuentoPorcentaje());
        promocion.setFechaInicio(dto.getFechaInicio());
        promocion.setFechaFin(dto.getFechaFin());
        if (dto.getImagenReferencia() != null && !dto.getImagenReferencia().isEmpty()) {
            // Convertir el texto (Base64) a byte[] para varbinary
            byte[] imagenBytes = Base64.getDecoder().decode(dto.getImagenReferencia());
            promocion.setImagenReferencia(imagenBytes);
        }
        return promocion;
    }
}
