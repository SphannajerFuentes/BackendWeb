/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Service;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.Direccion;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.Rol;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.Usuario;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Mapper.UserLoginRegisterMapper;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Repository.DireccionRepository;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Repository.RolRepository;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Repository.UserRepository;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Repository.UsuarioRepository;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.RegistroUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author danie
 */
@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RolRepository rolRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private EmailService emailService;

    @Autowired
    private final UsuarioRepository usuarioRepository;

    @Autowired
    private final UserLoginRegisterMapper userMapper;

    @Autowired
    private final DireccionRepository direccionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RolRepository rolRepository, UsuarioRepository usuarioRepository, UserLoginRegisterMapper userMapper, DireccionRepository direccionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.userMapper = userMapper;
        this.direccionRepository = direccionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //  REGISTRO
    public boolean registrarUsuario(RegistroUserDTO dto) {
        try {
            // 1. Buscar el rol por defecto
            Rol rolPorDefecto = rolRepository.findBynombreRol("Cliente");
            if (rolPorDefecto == null) {
                return false;
            }

            // 2. Crear el Usuario sin dirección ni contraseña aún
            Usuario nuevo = userMapper.toEntityFromRegister(dto, rolPorDefecto);

            // 3. Crear la Dirección desde el DTO
            Direccion dir = new Direccion();
            dir.setDistrito(dto.getDistrito());
            dir.setCalle(dto.getCalle());
            dir.setNumero(dto.getNumero());
            dir.setCiudad(dto.getCiudad());
            dir.setReferencia(dto.getReferencia());

            // 4. Guardar primera la Dirección
            Direccion dirGuardada = direccionRepository.save(dir);

            // 5. Asignar dirección al usuario
            nuevo.setDireccion(dirGuardada);

            // 6. Encriptar contraseña
            String passwordEncriptada = passwordEncoder.encode(dto.getPassword());
            nuevo.setContrasena(passwordEncriptada);

            // 7. Guardar usuario
            Usuario guardado = userRepository.save(nuevo);

            return guardado != null && guardado.getIdUsuario() != null;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //  LOGIN
    public boolean login(String correo, String contrasenaIngresada) { //Loguin que funciona con BCrypt. Compara la contraseña ingresada con la almacenada en la bd que esta cifrada
        // Buscar el usuario solo por correo
        return userRepository.findByCorreo(correo)
                .map(usuario -> {
                    // Obtener la contraseña (hash) guardada en BD
                    String contrasenaEnBD = usuario.getContrasena();

                    // Comparar usando BCrypt
                    return passwordEncoder.matches(contrasenaIngresada, contrasenaEnBD);
                })
                .orElse(false);
    }

    //  RECUPERAR CONTRASEÑA
    public String recuperarContrasena(String correo) {
        return userRepository.findByCorreo(correo)
                .map(Usuario::getContrasena)
                .orElse(null);
    }

    public Usuario getUsuarioByCorreo(String correo) {
        return userRepository.findByCorreo(correo).orElse(null);
    }

    public boolean enviarCodigoRecuperacion(String correo) {

        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) {
            return false;
        }

        // Generar código aleatorio de 6 dígitos
        String codigo = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Guardar en cache 'codigos'
        cacheManager.getCache("codigos").put(correo, codigo);

        // Enviar correo
        emailService.enviarCorreo(correo, "Código de Recuperación",
                "Tu código de recuperación es: " + codigo);

        return true;
    }

// Método opcional para validar el código ingresado
    public boolean validarCodigo(String correo, String codigoIngresado) {
        String codigoGuardado = cacheManager.getCache("codigos").get(correo, String.class);
        return codigoGuardado != null && codigoGuardado.equals(codigoIngresado);
    }

    public boolean actualizarContrasena(String correo, String nuevaContrasena) {
        Usuario user = usuarioRepository.findByCorreo(correo);
        if (user == null) {
            return false;
        }

        // Encriptar nueva contraseña con BCrypt
        user.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(user);
        return true;
    }

}
