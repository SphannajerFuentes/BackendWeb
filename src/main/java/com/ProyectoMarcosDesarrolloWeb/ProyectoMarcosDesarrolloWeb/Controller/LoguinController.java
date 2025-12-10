/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Controller;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.Direccion;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.Usuario;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Service.EmailService;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Service.UserService;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.JwtResponseDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.LoguinUserDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.RegistroUserDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.security.CustomUserDetails;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.util.JwtUtil;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author danie
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class LoguinController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private JwtUtil jwtUtil;

    // LOGIN ahora devuelve token JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoguinUserDTO userDTO) {
        String correo = userDTO.getCorreo();
        String contrasena = userDTO.getContrasena();

        Usuario usuario = userService.getUsuarioByCorreo(correo);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        boolean passwordCorrect = userService.login(correo, contrasena);
        if (!passwordCorrect) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }

        // Generar token JWT
        String token = jwtUtil.generateToken(usuario.getCorreo(), usuario.getRol().getNombreRol());

        // Devolver token + datos del usuario
        JwtResponse response = new JwtResponse(
                token,
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().getNombreRol()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistroUserDTO userDTO) {
        if (userService.recuperarContrasena(userDTO.getCorreo()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El correo ya está registrado");
        }

        userService.registrarUsuario(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuario registrado correctamente");
    }

    // GET Perfil del usuario autenticado (con dirección)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'Cliente')")
    @GetMapping("/perfil")
    public ResponseEntity<?> getPerfil(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUsuario() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        Usuario usuario = userDetails.getUsuario();
        Map<String, Object> response = new HashMap<>();
        response.put("idUsuario", usuario.getIdUsuario());
        response.put("nombre", usuario.getNombre());
        response.put("correo", usuario.getCorreo());
        response.put("telefono", usuario.getTelefono());

        // Agregar dirección si existe
        Direccion direccion = usuario.getDireccion();
        if (direccion != null) {
            Map<String, Object> dirMap = new HashMap<>();
            dirMap.put("id", direccion.getId_Direccion());
            dirMap.put("calle", direccion.getCalle());
            dirMap.put("numero", direccion.getNumero());
            dirMap.put("distrito", direccion.getDistrito());
            dirMap.put("ciudad", direccion.getCiudad());
            dirMap.put("referencia", direccion.getReferencia());
            response.put("direccion", dirMap);
        } else {
            response.put("direccion", null);
        }

        return ResponseEntity.ok(response);
    }

    // Clase interna para la respuesta con JWT
    public static class JwtResponse {

        private String token;
        private Long idUsuario;
        private String nombre;
        private String correo;
        private String rol;

        public JwtResponse(String token, Long idUsuario, String nombre, String correo, String rol) {
            this.token = token;
            this.idUsuario = idUsuario;
            this.nombre = nombre;
            this.correo = correo;
            this.rol = rol;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Long getIdUsuario() {
            return idUsuario;
        }

        public void setIdUsuario(Long idUsuario) {
            this.idUsuario = idUsuario;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public String getRol() {
            return rol;
        }

        public void setRol(String rol) {
            this.rol = rol;
        }
    }
}
