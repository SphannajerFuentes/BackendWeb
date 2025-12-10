/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Controller;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.Usuario;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Service.EmailService;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Service.UserService;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.CodigoRequestDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.ValidarCodigoDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.util.JwtUtil;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author danie
 */
@RestController
@RequestMapping("/api/recuperar")
public class RecuperarContrasenaController {
    
     @Autowired
    private EmailService emailService;
     
     @Autowired
     private JwtUtil jwtUtil;

    @Autowired
    private CacheManager cacheManager;
    
    @Autowired
    private UserService userService;
    
    // Enviar codigo
    @PostMapping("/enviar-codigo")
    public ResponseEntity<?> enviarCodigo(@RequestBody CodigoRequestDTO dto) {

        boolean enviado = userService.enviarCodigoRecuperacion(dto.getCorreo());

        if (!enviado) {
            return ResponseEntity.badRequest()
                    .body("No existe un usuario con ese correo");
        }

        return ResponseEntity.ok("Código enviado al correo");
    }
    
    @PostMapping("/verificar-correo")
    public ResponseEntity<?> verificarCorreo(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        Usuario usuario = userService.getUsuarioByCorreo(correo);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Correo no registrado");
        }

        // Generar token temporal para recuperación (expira en 5 min)
        String tokenTemporal = jwtUtil.generateToken(usuario.getCorreo(), usuario.getRol().getNombreRol());

        return ResponseEntity.ok(Map.of("token", tokenTemporal));
    }

    // Validar código
    @PostMapping("/validar-codigo")
    public ResponseEntity<?> validarCodigo(@RequestBody ValidarCodigoDTO dto) {

        boolean valido = userService.validarCodigo(dto.getCorreo(), dto.getCodigo());

        if (!valido) {
            return ResponseEntity.badRequest()
                    .body("Código incorrecto o expirado");
        }

        return ResponseEntity.ok("Código válido");
    }

    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<?> recuperarContrasena(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        String codigo = body.get("codigo");
        String nueva = body.get("nuevaContrasena");

        boolean valido = userService.validarCodigo(correo, codigo);
        if (!valido) {
            return ResponseEntity.badRequest().body("Código incorrecto o expirado");
        }

        boolean actualizado = userService.actualizarContrasena(correo, nueva);
        if (!actualizado) {
            return ResponseEntity.badRequest().body("No se pudo actualizar la contraseña");
        }

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }
}
