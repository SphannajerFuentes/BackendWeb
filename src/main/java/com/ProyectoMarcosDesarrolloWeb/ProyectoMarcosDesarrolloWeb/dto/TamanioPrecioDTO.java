package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto;

import java.math.BigDecimal; // Usa BigDecimal o Double según tu tipo de dato

public class TamanioPrecioDTO {

    private Long idTamanio;
    private Long idProducto;
    private String nombreTamanio; // Viene de Tamanio_Pizza.nombre
    private BigDecimal precio;

    // Constructor vacío (necesario)
    public TamanioPrecioDTO() {
    }

    public TamanioPrecioDTO(Long idTamanio, Long idProducto, String nombreTamanio, BigDecimal precio) {
        this.idTamanio = idTamanio;
        this.idProducto = idProducto;
        this.nombreTamanio = nombreTamanio;
        this.precio = precio;
    }

    public Long getIdTamanio() {
        return idTamanio;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdTamanio(Long idTamanio) {
        this.idTamanio = idTamanio;
    }

    public String getNombreTamanio() {
        return nombreTamanio;
    }

    public void setNombreTamanio(String nombreTamanio) {
        this.nombreTamanio = nombreTamanio;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
