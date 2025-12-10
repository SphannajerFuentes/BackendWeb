package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Controller;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Service.ReporteService;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.ReporteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    // GET /api/reportes - Obtener reporte completo
    @GetMapping
    public ReporteDTO obtenerReporte() {
        return reporteService.obtenerReporteCompleto();
    }
}
