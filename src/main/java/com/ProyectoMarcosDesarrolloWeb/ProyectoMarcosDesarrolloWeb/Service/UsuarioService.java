/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Service;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.Rol;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.Usuario;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.UsuarioPerfilDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.CambioContrasenaDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Mapper.UsuarioMapper;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Repository.RolRepository;
import org.springframework.stereotype.Service;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Repository.UsuarioRepository;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.UsuarioAdminDTO;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository;
    }

    // Obtener perfil
    public UsuarioPerfilDTO obtenerPerfil(Long idUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        return usuarioOpt.map(usuarioMapper::toDTO).orElse(null);
    }

    // Actualizar perfil
    public UsuarioPerfilDTO actualizarPerfil(Long idUsuario, UsuarioPerfilDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuarioMapper.actualizarEntidadDesdeDTO(usuario, dto);
            Usuario actualizado = usuarioRepository.save(usuario);
            return usuarioMapper.toDTO(actualizado);
        }
        return null;
    }

    // Cambiar contraseña usando BCrypt
    public boolean cambiarContrasena(Long idUsuario, CambioContrasenaDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // compara contraseña actual en texto con el hash almacenado
            if (!passwordEncoder.matches(dto.getContrasenaActual(), usuario.getContrasena())) {
                return false;
            }

            // guarda nueva contraseña cifrada
            String nuevaEncriptada = passwordEncoder.encode(dto.getNuevaContrasena());
            usuario.setContrasena(nuevaEncriptada);
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }

    // Obtener todos los usuarios para admin
    public List<UsuarioAdminDTO> obtenerTodosParaAdmin() {
        return usuarioRepository.findAll().stream()
                .map(this::usuarioToAdminDTO)
                .collect(Collectors.toList());
    }

    // Crear nuevo usuario
    public UsuarioAdminDTO crearUsuario(UsuarioAdminDTO dto) {
        // Verificar que el correo no exista
        if (usuarioRepository.findByCorreo(dto.getCorreo()) != null) {
            throw new RuntimeException("Ya existe un usuario con ese correo");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setCorreo(dto.getCorreo());
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));

        // Parsear teléfono
        if (dto.getTelefono() != null && !dto.getTelefono().isEmpty()) {
            try {
                usuario.setTelefono(Long.parseLong(dto.getTelefono()));
            } catch (NumberFormatException e) {
                usuario.setTelefono(0);
            }
        }

        // Asignar rol
        if (dto.getIdRol() != null) {
            Optional<Rol> rolOpt = rolRepository.findById(dto.getIdRol());
            rolOpt.ifPresent(usuario::setRol);
        }

        Usuario guardado = usuarioRepository.save(usuario);
        return usuarioToAdminDTO(guardado);
    }

    // Actualizar usuario desde admin
    public UsuarioAdminDTO actualizarUsuarioAdmin(Long idUsuario, UsuarioAdminDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar que el correo no esté en uso por otro usuario
        Usuario existente = usuarioRepository.findByCorreo(dto.getCorreo());
        if (existente != null && !existente.getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("Ya existe otro usuario con ese correo");
        }

        usuario.setNombre(dto.getNombre());
        usuario.setCorreo(dto.getCorreo());

        // Actualizar contraseña solo si se proporciona
        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }

        // Parsear teléfono
        if (dto.getTelefono() != null && !dto.getTelefono().isEmpty()) {
            try {
                usuario.setTelefono(Long.parseLong(dto.getTelefono()));
            } catch (NumberFormatException e) {
                // Mantener teléfono actual si hay error
            }
        }

        // Actualizar rol
        if (dto.getIdRol() != null) {
            Optional<Rol> rolOpt = rolRepository.findById(dto.getIdRol());
            rolOpt.ifPresent(usuario::setRol);
        }

        Usuario guardado = usuarioRepository.save(usuario);
        return usuarioToAdminDTO(guardado);
    }

    // Eliminar usuario
    public boolean eliminarUsuario(Long idUsuario) {
        if (usuarioRepository.existsById(idUsuario)) {
            usuarioRepository.deleteById(idUsuario);
            return true;
        }
        return false;
    }

    // Helper: Convertir Usuario a UsuarioAdminDTO
    private UsuarioAdminDTO usuarioToAdminDTO(Usuario usuario) {
        UsuarioAdminDTO dto = new UsuarioAdminDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setCorreo(usuario.getCorreo());
        dto.setTelefono(String.valueOf(usuario.getTelefono()));

        if (usuario.getRol() != null) {
            dto.setIdRol(usuario.getRol().getId_Rol()); // long primitivo se convierte automáticamente
            dto.setRol(usuario.getRol().getNombreRol());
        }

        return dto;
    }

}
