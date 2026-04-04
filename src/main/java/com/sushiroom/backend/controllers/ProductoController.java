package com.sushiroom.backend.controllers;

import com.sushiroom.backend.dto.ProductoDTO;
import com.sushiroom.backend.models.Categoria;
import com.sushiroom.backend.models.Producto;
import com.sushiroom.backend.services.CategoriaService;
import com.sushiroom.backend.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @PostConstruct
    public void init() {
        categoriaService.inicializarCategoriasBasicas();
    }

    @GetMapping
    public List<ProductoDTO> getAllProductos() {
        List<Producto> productos = productoService.findAllActivos();
        return productos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProductoDTO getProductoById(@PathVariable Integer id) {
        Producto producto = productoService.findById(id);
        return convertToDTO(producto);
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<Categoria>> getCategorias() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @GetMapping("/categorias/activas")
    public ResponseEntity<List<Categoria>> getCategoriasActivas() {
        List<Categoria> categorias = categoriaService.findCategoriasWithProductosActivos();
        return ResponseEntity.ok(categorias);
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> createProducto(@RequestBody ProductoDTO productoDTO) {
        try {
            Producto producto = convertToEntity(productoDTO);
            Producto nuevoProducto = productoService.save(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(nuevoProducto));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> updateProducto(@PathVariable Integer id, @RequestBody ProductoDTO productoDTO) {
        try {
            Producto productoExistente = productoService.findById(id);
            if (productoExistente == null) {
                return ResponseEntity.notFound().build();
            }

            // Actualizar campos
            productoExistente.setNombre(productoDTO.getNombre());
            productoExistente.setDescripcion(productoDTO.getDescripcion());
            productoExistente.setOrigen(productoDTO.getOrigen());
            productoExistente.setNotasCata(productoDTO.getNotasCata());
            productoExistente.setPrecio(productoDTO.getPrecio());
            productoExistente.setImagenUrl(productoDTO.getImagenUrl());
            productoExistente.setActivo(true);

            // Actualizar categoría
            if (productoDTO.getCategoriaId() != null) {
                Categoria categoria = categoriaService.findById(productoDTO.getCategoriaId());
                productoExistente.setCategoria(categoria);
            }
            
            Producto productoActualizado = productoService.save(productoExistente);
            return ResponseEntity.ok(convertToDTO(productoActualizado));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Integer id) {
        try {
            Producto producto = productoService.findById(id);
            if (producto == null) {
                return ResponseEntity.notFound().build();
            }

            // Soft delete - solo desactivar
            producto.setActivo(false);
            productoService.save(producto);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<ProductoDTO> toggleDisponibilidad(@PathVariable Integer id) {
        try {
            Producto producto = productoService.findById(id);
            if (producto == null) {
                return ResponseEntity.notFound().build();
            }

            producto.setActivo(!producto.getActivo());
            Producto productoActualizado = productoService.save(producto);
            return ResponseEntity.ok(convertToDTO(productoActualizado));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Convertir DTO a Entidad
    private Producto convertToEntity(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setOrigen(dto.getOrigen());
        producto.setNotasCata(dto.getNotasCata());
        producto.setPrecio(dto.getPrecio());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        // Asignar categoría
        if (dto.getCategoriaId() != null) {
            try {
                Categoria categoria = categoriaService.findById(dto.getCategoriaId());
                producto.setCategoria(categoria);
            } catch (Exception e) {
                System.err.println("Error al buscar categoría por ID: " + e.getMessage());
            }
        } else if (dto.getCategoriaNombre() != null && !dto.getCategoriaNombre().isEmpty()) {
            try {
                Categoria categoria = categoriaService.findByNombre(dto.getCategoriaNombre().toLowerCase());
                producto.setCategoria(categoria);
            } catch (Exception e) {
                System.err.println("Error al buscar categoría por nombre: " + e.getMessage());
                // Si no existe, crear una nueva categoría
                Categoria nuevaCategoria = new Categoria();
                nuevaCategoria.setNombre(dto.getCategoriaNombre().toLowerCase());
                nuevaCategoria.setDescripcion("Categoría: " + dto.getCategoriaNombre());
                categoriaService.save(nuevaCategoria);
                producto.setCategoria(nuevaCategoria);
            }
        }

        return producto;
    }

    // Convertir Entidad a DTO
    private ProductoDTO convertToDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setOrigen(producto.getOrigen());
        dto.setNotasCata(producto.getNotasCata());
        dto.setPrecio(producto.getPrecio());
        dto.setImagenUrl(producto.getImagenUrl());
        dto.setActivo(producto.getActivo());

        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getId());
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
        }

        return dto;
    }
}