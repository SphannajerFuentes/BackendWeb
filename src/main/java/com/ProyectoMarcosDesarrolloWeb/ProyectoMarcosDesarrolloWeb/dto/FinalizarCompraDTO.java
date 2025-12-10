package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto;

import java.util.List;

public class FinalizarCompraDTO {
    
    private Long idMetodoPago;
    private Long idTipoEnvio;
    private List<CarritoItemDTO> productos;

    // Getters y Setters
    public Long getIdMetodoPago() {
        return idMetodoPago;
    }

    public void setIdMetodoPago(Long idMetodoPago) {
        this.idMetodoPago = idMetodoPago;
    }

    public Long getIdTipoEnvio() {
        return idTipoEnvio;
    }

    public void setIdTipoEnvio(Long idTipoEnvio) {
        this.idTipoEnvio = idTipoEnvio;
    }

    public List<CarritoItemDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<CarritoItemDTO> productos) {
        this.productos = productos;
    }
}
