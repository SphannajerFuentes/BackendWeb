package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Controller;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Service.PromocionService;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.PromocionesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/promociones")
@CrossOrigin(origins = "*")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'Cliente')")
    @GetMapping
    public List<PromocionesDTO> listar() {
        return promocionService.listarPromociones();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'Cliente')")
    @GetMapping("/{id}")
    public PromocionesDTO obtenerPorId(@PathVariable Long id) {
        return promocionService.obtenerPorId(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'Cliente')")
    @PostMapping
    public PromocionesDTO crear(@RequestBody PromocionesDTO dto) {
        return promocionService.guardar(dto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'Cliente')")
    @PutMapping("/{id}")
    public PromocionesDTO actualizar(@PathVariable Long id, @RequestBody PromocionesDTO dto) {
        dto.setIdPromocion(id);
        return promocionService.guardar(dto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'Cliente')")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        promocionService.eliminar(id);
    }
    
    @PreAuthorize("hasAnyAuthority('ADMIN', 'Cliente')")
    @GetMapping("/{id}/productos")
    public ResponseEntity<Set<Long>> obtenerProductosDePromocion(@PathVariable Long id) {
        Set<Long> productosIds = promocionService.obtenerProductosIdsPorPromocion(id);
        return ResponseEntity.ok(productosIds);
    }
}