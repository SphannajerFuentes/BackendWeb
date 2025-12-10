package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Service;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.CompraDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.CarritoItemDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.VentaPuntoVentaDTO;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Mapper.CompraMapper;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Repository.*;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private CompraMapper compraMapper;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoProductoRepository carritoProductoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    @Autowired
    private TipoEnvioRepository tipoEnvioRepository;

    @Autowired
    private CompraProductoRepository compraProductoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ExtraRepository extraRepository;

    /**
     * Procesar venta desde Punto de Venta (admin) - sin usuario cliente
     */
    @Transactional
    public CompraDTO procesarVentaPuntoVenta(VentaPuntoVentaDTO request) {
        // 1. Obtener método de pago por nombre
        Metodo_Pago metodoPago = metodoPagoRepository.findByNombre(request.getMetodoPago())
                .orElseGet(() -> metodoPagoRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado")));

        // 2. Obtener tipo de envío por descripción
        Tipo_Envio tipoEnvio = tipoEnvioRepository.findByDescripcion(request.getTipoEnvio())
                .orElseGet(() -> tipoEnvioRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Tipo de envío no encontrado")));

        // 3. Usar el precio total enviado desde el frontend
        BigDecimal total = request.getPrecioTotal();
        if (total == null) {
            total = BigDecimal.ZERO;
            for (VentaPuntoVentaDTO.ProductoVentaDTO item : request.getProductos()) {
                BigDecimal precioItem = item.getPrecioUnitario() != null
                        ? item.getPrecioUnitario()
                        : BigDecimal.valueOf(10.00);
                BigDecimal subtotal = precioItem.multiply(BigDecimal.valueOf(item.getCantidad()));
                total = total.add(subtotal);
            }
        }

        // 4. Crear la compra (sin usuario - venta directa en local)
        Compra nuevaCompra = new Compra();
        nuevaCompra.setUsuario(null); // Venta en mostrador sin usuario registrado
        nuevaCompra.setMetodoPago(metodoPago);
        nuevaCompra.setTipo_envio(tipoEnvio);
        nuevaCompra.setFecha_Compra(LocalDateTime.now());
        nuevaCompra.setPrecio_total(total);

        // Guardar la compra
        Compra compraSaved = compraRepository.save(nuevaCompra);

        // 5. Crear CompraProducto para cada producto
        List<CompraProducto> compraProductos = new ArrayList<>();
        for (VentaPuntoVentaDTO.ProductoVentaDTO item : request.getProductos()) {
            Producto producto = productoRepository.findById(item.getIdProducto()).orElse(null);

            if (producto != null) {
                CompraProducto cp = new CompraProducto();

                CompraProductoId cpId = new CompraProductoId();
                cpId.setId_Compra(compraSaved.getId_Compra());
                cpId.setId_Producto(producto.getIdProducto());
                cp.setId(cpId);

                cp.setCompra(compraSaved);
                cp.setProducto(producto);
                cp.setCantidad(item.getCantidad());

                BigDecimal precioVenta = item.getPrecioUnitario() != null
                        ? item.getPrecioUnitario()
                        : BigDecimal.valueOf(10.00);
                cp.setPrecio_venta(precioVenta);

                // Guardar el primer extra si existe
                if (item.getIdExtras() != null && !item.getIdExtras().isEmpty()) {
                    Long primerIdExtra = item.getIdExtras().get(0);
                    Extra extra = extraRepository.findById(primerIdExtra).orElse(null);
                    if (extra != null) {
                        cp.setExtra(extra);
                    }
                }

                compraProductos.add(cp);
            }
        }

        if (!compraProductos.isEmpty()) {
            compraProductoRepository.saveAll(compraProductos);
        }

        return compraMapper.toDto(compraSaved);
    }

    /**
     * Procesar compra recibiendo los productos desde el frontend (localStorage)
     */
    @Transactional
    public CompraDTO procesarCompraConProductos(Long idUsuario, Long idMetodoPago, Long idTipoEnvio,
            List<CarritoItemDTO> productosCarrito) {
        // 1. Obtener usuario
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        // 2. Obtener método de pago
        Metodo_Pago metodoPago = metodoPagoRepository.findById(idMetodoPago)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado con ID: " + idMetodoPago));

        // 3. Obtener tipo de envío
        Tipo_Envio tipoEnvio = tipoEnvioRepository.findById(idTipoEnvio)
                .orElseThrow(() -> new RuntimeException("Tipo de envío no encontrado con ID: " + idTipoEnvio));

        // 4. Calcular total basado en los productos recibidos
        BigDecimal total = BigDecimal.ZERO;
        for (CarritoItemDTO item : productosCarrito) {
            BigDecimal precioItem = item.getPrecioUnitario() != null
                    ? item.getPrecioUnitario()
                    : BigDecimal.valueOf(10.00); // Precio por defecto si no viene
            BigDecimal subtotal = precioItem.multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);
        }

        // Agregar costo de envío
        if (tipoEnvio.getCosto_envio() != null) {
            total = total.add(tipoEnvio.getCosto_envio());
        }

        // 6. Crear la compra
        Compra nuevaCompra = new Compra();
        nuevaCompra.setUsuario(usuario);
        nuevaCompra.setMetodoPago(metodoPago);
        nuevaCompra.setTipo_envio(tipoEnvio);
        nuevaCompra.setFecha_Compra(LocalDateTime.now());
        nuevaCompra.setPrecio_total(total);

        // Guardar la compra
        Compra compraSaved = compraRepository.save(nuevaCompra);

        // 6. Crear CompraProducto para cada producto del carrito
        List<CompraProducto> compraProductos = new ArrayList<>();
        for (CarritoItemDTO item : productosCarrito) {
            // Buscar el producto en la BD
            Producto producto = productoRepository.findById(item.getIdProducto())
                    .orElse(null);

            if (producto != null) {
                CompraProducto cp = new CompraProducto();

                // Crear el ID compuesto
                CompraProductoId cpId = new CompraProductoId();
                cpId.setId_Compra(compraSaved.getId_Compra());
                cpId.setId_Producto(producto.getIdProducto());
                cp.setId(cpId);

                cp.setCompra(compraSaved);
                cp.setProducto(producto);
                cp.setCantidad(item.getCantidad());

                // Usar el precio que viene del frontend
                BigDecimal precioVenta = item.getPrecioUnitario() != null
                        ? item.getPrecioUnitario()
                        : BigDecimal.valueOf(10.00);
                cp.setPrecio_venta(precioVenta);

                // Guardar el primer extra si existe
                if (item.getIdExtras() != null && !item.getIdExtras().isEmpty()) {
                    Long primerIdExtra = item.getIdExtras().get(0);
                    Extra extra = extraRepository.findById(primerIdExtra).orElse(null);
                    if (extra != null) {
                        cp.setExtra(extra);
                    }
                }

                compraProductos.add(cp);
            }
        }

        if (!compraProductos.isEmpty()) {
            compraProductoRepository.saveAll(compraProductos);
        }

        return compraMapper.toDto(compraSaved);
    }

    @Transactional
    public CompraDTO procesarCompra(Long idUsuario, Long idMetodoPago, Long idTipoEnvio) {
        // 1. Obtener usuario
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        // 2. Obtener método de pago
        Metodo_Pago metodoPago = metodoPagoRepository.findById(idMetodoPago)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado con ID: " + idMetodoPago));

        // 3. Obtener tipo de envío
        Tipo_Envio tipoEnvio = tipoEnvioRepository.findById(idTipoEnvio)
                .orElseThrow(() -> new RuntimeException("Tipo de envío no encontrado con ID: " + idTipoEnvio));

        // 4. Obtener el carrito del usuario (o crear uno nuevo si no existe)
        Carrito carrito = carritoRepository.findByUsuarioIdUsuario(idUsuario);
        if (carrito == null) {
            // Crear carrito automáticamente si no existe
            carrito = new Carrito();
            carrito.setUsuario(usuario);
            carrito = carritoRepository.save(carrito);
        }

        // 5. Obtener productos del carrito de la BD
        List<CarritoProducto> itemsCarrito = carritoProductoRepository.findByCarrito(carrito);

        // Si el carrito en BD está vacío, es porque el frontend usa localStorage
        // Por ahora permitimos compras "directas" sin carrito en BD
        if (itemsCarrito.isEmpty()) {
            // Permitir la compra sin productos en el carrito de BD
            // El frontend manejará los productos desde localStorage
            System.out.println("ADVERTENCIA: Carrito vacío en BD, procesando compra sin productos del carrito de BD");
        }

        // 6. Calcular total basado en precio temporal (TODO: implementar relación con TamanioProductoPrecio)
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal precioTemporalPorProducto = BigDecimal.valueOf(10.00); // Precio temporal

        for (CarritoProducto item : itemsCarrito) {
            BigDecimal subtotal = precioTemporalPorProducto.multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);
        }

        // Agregar costo de envío usando el campo correcto
        if (tipoEnvio.getCosto_envio() != null) {
            total = total.add(tipoEnvio.getCosto_envio());
        }

        // 7. Crear la compra
        Compra nuevaCompra = new Compra();
        nuevaCompra.setUsuario(usuario);
        nuevaCompra.setMetodoPago(metodoPago);
        nuevaCompra.setTipo_envio(tipoEnvio);
        nuevaCompra.setFecha_Compra(LocalDateTime.now());
        nuevaCompra.setPrecio_total(total);

        // Guardar la compra
        Compra compraSaved = compraRepository.save(nuevaCompra);

        // 8. Crear CompraProducto para cada item del carrito
        List<CompraProducto> compraProductos = new ArrayList<>();
        for (CarritoProducto item : itemsCarrito) {
            CompraProducto cp = new CompraProducto();

            // Crear el ID compuesto
            CompraProductoId cpId = new CompraProductoId();
            cpId.setId_Compra(compraSaved.getId_Compra());
            cpId.setId_Producto(item.getProducto().getIdProducto());
            cp.setId(cpId);

            cp.setCompra(compraSaved);
            cp.setProducto(item.getProducto());
            cp.setCantidad(item.getCantidad());
            // Usar precio temporal (TODO: obtener precio real desde TamanioProductoPrecio)
            cp.setPrecio_venta(precioTemporalPorProducto);

            compraProductos.add(cp);
        }
        compraProductoRepository.saveAll(compraProductos);

        // 9. Vaciar el carrito del usuario
        carritoProductoRepository.deleteAll(itemsCarrito);

        return compraMapper.toDto(compraSaved);
    }

    public List<CompraDTO> obtenerHistorial(Long idUsuario) {
        return compraMapper.toDtoList(compraRepository.findByUsuarioIdUsuario(idUsuario));
    }

    public List<CompraDTO> obtenerTodasLasCompras() {
        return compraMapper.toDtoList(compraRepository.findAll());
    }
}
