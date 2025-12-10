package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto;

public class TamanioPizzaDTO {

    private Long idTamanio;
    private String nombre;

    // Constructor vacío y con campos (necesarios)
    public TamanioPizzaDTO() {
    }

    public TamanioPizzaDTO(Long idTamanio, String nombre) {
        this.idTamanio = idTamanio;
        this.nombre = nombre;
    }

    // --- Getters y Setters ---
    public Long getIdTamanio() {
        return idTamanio;
    }

    public void setIdTamanio(Long idTamanio) {
        this.idTamanio = idTamanio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
