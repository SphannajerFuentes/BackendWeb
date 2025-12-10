package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Controller;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.TamanioPrecioDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Service.TamanioProductoPrecioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/precios")
@CrossOrigin(origins = "*")
public class TamanioProductoPrecioController {

    @Autowired
    private TamanioProductoPrecioService service;

    // Endpoint: http://localhost:8080/api/precios/{idProducto}
    @GetMapping("/{idProducto}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'Cliente')")
    public List<TamanioPrecioDTO> obtenerPreciosPorProducto(@PathVariable Long idProducto) {
        return service.findPreciosByProductoId(idProducto);
    }

    // POST: Crear nueva relación tamaño-precio para un producto
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'Cliente')")
    public TamanioPrecioDTO crearTamanioPrecio(@RequestBody TamanioPrecioDTO dto) {
        return service.crearTamanioPrecio(dto);
    }

    // PUT: Editar el precio de un tamaño de producto
    @PutMapping("/{idTamanio}/{idProducto}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'Cliente')")
    public ResponseEntity<TamanioPrecioDTO> editarTamanioPrecio(
            @PathVariable Long idTamanio,
            @PathVariable Long idProducto,
            @RequestBody TamanioPrecioDTO dto) {
        TamanioPrecioDTO actualizado = service.editarTamanioPrecio(idTamanio, idProducto, dto);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE: Eliminar la relación tamaño-precio
    @DeleteMapping("/{idTamanio}/{idProducto}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'Cliente')")
    public ResponseEntity<Void> eliminarTamanioPrecio(
            @PathVariable Long idTamanio,
            @PathVariable Long idProducto) {
        boolean eliminado = service.eliminarTamanioPrecio(idTamanio, idProducto);
        if (eliminado) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
