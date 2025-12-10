package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Controller;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Service.TamanioPizzaService;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.TamanioPizzaDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
// El frontend lo llamará con: http://localhost:8080/api/tamanios
@PreAuthorize("hasAnyAuthority('ADMIN', 'Cliente')")
@RequestMapping("/api/tamanios")
public class TamanioPizzaController {

    @Autowired
    private TamanioPizzaService tamanioPizzaService;

    @GetMapping
    public List<TamanioPizzaDTO> obtenerTodosLosTamanios() {
        return tamanioPizzaService.findAll();
    }
}
