package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.security;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.Usuario;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService { //Pasa el objeto de tipo Usuario a CustomUserDetails para volverlo un objeto que Spring Security entiende

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) //Este metodo busca al usuario por su correo y si lo encuentra lo almacena en un objeto Usuario y con él creamos un objeto que Spring security puede entender para autenticarlo
            throws UsernameNotFoundException {

        Usuario usuario = userRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("No existe usuario con correo: " + correo));

        return new CustomUserDetails(usuario); //Objeto que Spring security sí puede entender para autenticar. 
    }
}
