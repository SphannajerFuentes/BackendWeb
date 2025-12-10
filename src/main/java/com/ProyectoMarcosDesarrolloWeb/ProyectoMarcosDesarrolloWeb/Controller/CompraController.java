package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Controller;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.CompraDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.FinalizarCompraDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Service.CompraService;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.VentaPuntoVentaDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@PreAuthorize("hasAnyAuthority('ADMIN', 'Cliente')")
@RequestMapping("/api/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    // Obtener el ID del usuario autenticado desde el token JWT
    private Long getCurrentUserId(CustomUserDetails userDetails) {
        if (userDetails != null && userDetails.getUsuario() != null) {
            return userDetails.getUsuario().getIdUsuario();
        }
        return null;
    }

    // ENDPOINT: GET /api/compras - Historial del usuario actual
    @GetMapping
    public ResponseEntity<?> getHistorialCompras(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = getCurrentUserId(userDetails);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario no autenticado"));
        }
        return ResponseEntity.ok(compraService.obtenerHistorial(userId));
    }

    // ENDPOINT: GET /api/compras/todas - Todas las compras (para admin)
    @GetMapping("/todas")
    public List<CompraDTO> getTodasLasCompras() {
        return compraService.obtenerTodasLasCompras();
    }

    // ENDPOINT: POST /api/compras/finalizar - Recibe productos desde el frontend
    @PostMapping("/finalizar")
    public ResponseEntity<?> finalizarCompra(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody FinalizarCompraDTO request) {
        try {
            Long userId = getCurrentUserId(userDetails);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Usuario no autenticado"));
            }

            if (request.getProductos() == null || request.getProductos().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El carrito está vacío"));
            }

            CompraDTO nuevaCompra = compraService.procesarCompraConProductos(
                    userId,
                    request.getIdMetodoPago(),
                    request.getIdTipoEnvio(),
                    request.getProductos()
            );
            return ResponseEntity.ok(nuevaCompra);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ENDPOINT: POST /api/compras - Venta directa desde Punto de Venta (admin)
    @PostMapping
    public ResponseEntity<?> crearVentaPuntoVenta(@RequestBody VentaPuntoVentaDTO request) {
        try {
            if (request.getProductos() == null || request.getProductos().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debe agregar al menos un producto"));
            }

            CompraDTO nuevaCompra = compraService.procesarVentaPuntoVenta(request);
            return ResponseEntity.ok(nuevaCompra);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    
}
