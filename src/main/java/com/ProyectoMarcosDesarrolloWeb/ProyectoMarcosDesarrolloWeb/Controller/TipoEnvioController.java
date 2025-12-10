package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Controller;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.Tipo_Envio;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Repository.TipoEnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/tipos-envio")
@CrossOrigin(origins = "*")
public class TipoEnvioController {

    @Autowired
    private TipoEnvioRepository tipoEnvioRepository;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'Cliente')")
    @GetMapping
    public List<Tipo_Envio> obtenerTodos() {
        return tipoEnvioRepository.findAll();
    }
}
