// Archivo: TamanioProductoPrecioService.java (Refactorizado)
package com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Service;

import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Repository.ProductoRepository;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Repository.TamanioPizzaRepository;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Repository.TamanioProductoPrecioRepository;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Entity.TamanioProductoPrecioId;
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.Mapper.TamanioProductoPrecioMapper; // ¡Importa el nuevo Mapper!
import com.ProyectoMarcosDesarrolloWeb.ProyectoMarcosDesarrolloWeb.dto.TamanioPrecioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class TamanioProductoPrecioService {

    @Autowired
    private TamanioProductoPrecioRepository repository;

    // 💡 INYECTAR EL MAPPER DEDICADO
    @Autowired
    private TamanioProductoPrecioMapper mapper;

    @Autowired
    private TamanioPizzaRepository tamanioRepo;

    @Autowired
    private ProductoRepository productoRepo;

    // Obtener los precios y tamaños para un ID de producto específico
    public List<TamanioPrecioDTO> findPreciosByProductoId(Long idProducto) {
        // 💡 DELEGAR EL MAPEO AL MAPPER
        return repository.findByProductoIdProducto(idProducto).stream()
                .map(mapper::toDTO) // Usa la referencia al método del Mapper
                .toList();
    }

    // Crear nueva relación tamaño-precio
    public TamanioPrecioDTO crearTamanioPrecio(TamanioPrecioDTO dto) {
        var tamanio = tamanioRepo.findById(dto.getIdTamanio()).orElse(null);
        var producto = productoRepo.findById(dto.getIdProducto()).orElse(null);
        var entidad = mapper.toEntity(dto, tamanio, producto);
        var guardado = repository.save(entidad);
        return mapper.toDTO(guardado);
    }

    // Editar el precio de un tamaño de producto
    public TamanioPrecioDTO editarTamanioPrecio(Long idTamanio, Long idProducto, TamanioPrecioDTO dto) {
        TamanioProductoPrecioId id = new TamanioProductoPrecioId(idTamanio, idProducto);
        var entidad = repository.findById(id).orElse(null);
        if (entidad == null) {
            return null;
        }
        entidad.setPrecio(dto.getPrecio());
        var actualizado = repository.save(entidad);
        return mapper.toDTO(actualizado);
    }

    // Eliminar la relación tamaño-precio
    public boolean eliminarTamanioPrecio(Long idTamanio, Long idProducto) {
        TamanioProductoPrecioId id = new TamanioProductoPrecioId(idTamanio, idProducto);
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
