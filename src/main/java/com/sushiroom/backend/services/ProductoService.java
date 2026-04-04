package com.sushiroom.backend.services;

import com.sushiroom.backend.models.Producto;
import com.sushiroom.backend.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }
    
    public List<Producto> findAllActivos() {
        return productoRepository.findByActivoTrue();
    }
    
    public Producto findById(Integer id) {
        return productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }
    
    @Transactional  // ← AGREGAR ESTO
    public Producto save(Producto producto) {
        System.out.println("=== GUARDANDO PRODUCTO ===");
        System.out.println("Nombre: " + producto.getNombre());
        System.out.println("Precio: " + producto.getPrecio());
        System.out.println("Categoria ID: " + (producto.getCategoria() != null ? producto.getCategoria().getId() : "null"));
        
        if (producto.getFechaCreacion() == null) {
            producto.setFechaCreacion(LocalDateTime.now());
        }
        
        Producto saved = productoRepository.save(producto);
        System.out.println("Producto guardado con ID: " + saved.getId());
        
        return saved;
    }
    
    @Transactional
    public void deleteById(Integer id) {
        productoRepository.deleteById(id);
    }
    
    public List<Producto> findByCategoriaId(Integer categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }
}