package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto;

import java.math.BigDecimal;
import java.util.List;

public class VentaPuntoVentaDTO {

    private BigDecimal precioTotal;
    private String tipoEnvio;  // "Local", "Delivery", "Recojo"
    private String metodoPago; // "Efectivo", "Tarjeta", "Yape"
    private String fechaCompra;
    private List<ProductoVentaDTO> productos;

    // DTO interno para productos
    public static class ProductoVentaDTO {

        private Long idProducto;
        private Long idTamanio;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private List<Long> idExtras;

        public Long getIdProducto() {
            return idProducto;
        }

        public void setIdProducto(Long idProducto) {
            this.idProducto = idProducto;
        }

        public Long getIdTamanio() {
            return idTamanio;
        }

        public void setIdTamanio(Long idTamanio) {
            this.idTamanio = idTamanio;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public List<Long> getIdExtras() {
            return idExtras;
        }

        public void setIdExtras(List<Long> idExtras) {
            this.idExtras = idExtras;
        }
    }

    // Getters y Setters
    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getTipoEnvio() {
        return tipoEnvio;
    }

    public void setTipoEnvio(String tipoEnvio) {
        this.tipoEnvio = tipoEnvio;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(String fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public List<ProductoVentaDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoVentaDTO> productos) {
        this.productos = productos;
    }
}
