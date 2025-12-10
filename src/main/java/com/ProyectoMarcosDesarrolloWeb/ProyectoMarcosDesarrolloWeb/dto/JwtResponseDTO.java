package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto;

public class JwtResponseDTO {

    private String token;
    private Long idUsuario;
    private String nombre;
    private String rol;

    public JwtResponseDTO(String token, Long idUsuario, String nombre, String rol, String correo) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }


}
