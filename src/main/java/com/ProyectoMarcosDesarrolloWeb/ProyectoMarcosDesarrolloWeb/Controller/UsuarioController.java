package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Controller;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.UsuarioPerfilDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.UsuarioAdminDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.CambioContrasenaDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET Todos los usuarios (para admin)
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UsuarioAdminDTO>> obtenerTodos() {
        List<UsuarioAdminDTO> usuarios = usuarioService.obtenerTodosParaAdmin();
        return ResponseEntity.ok(usuarios);
    }

    // GET Perfil
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UsuarioPerfilDTO> obtenerPerfil(@PathVariable Long id) {
        UsuarioPerfilDTO dto = usuarioService.obtenerPerfil(id);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    
    //Crear un usuario nuevo
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioAdminDTO dto) {
        try {
            UsuarioAdminDTO nuevo = usuarioService.crearUsuario(dto);
            return ResponseEntity.ok(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT Actualizar perfil
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UsuarioPerfilDTO> actualizarPerfil(
            @PathVariable Long id,
            @RequestBody UsuarioPerfilDTO dto
    ) {
        UsuarioPerfilDTO actualizado = usuarioService.actualizarPerfil(id, dto);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.notFound().build();
    }

    // PUT Actualizar usuario (admin)
    @PutMapping("/{id}/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> actualizarUsuarioAdmin(
            @PathVariable Long id,
            @RequestBody UsuarioAdminDTO dto
    ) {
        try {
            UsuarioAdminDTO actualizado = usuarioService.actualizarUsuarioAdmin(id, dto);
            if (actualizado != null) {
                return ResponseEntity.ok(actualizado);
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE Eliminar usuario
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        boolean eliminado = usuarioService.eliminarUsuario(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("message", "Usuario eliminado correctamente"));
        }
        return ResponseEntity.notFound().build();
    }

    // PUT Cambiar contraseña
    @PutMapping("/{id}/cambiar-password")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> cambiarPassword(
            @PathVariable Long id,
            @RequestBody CambioContrasenaDTO dto
    ) {
        boolean ok = usuarioService.cambiarContrasena(id, dto);
        if (ok) {
            return ResponseEntity.ok("Contraseña actualizada correctamente");
        }
        return ResponseEntity.badRequest().body("Error al cambiar la contraseña");
    }
}
