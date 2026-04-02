// services/ProductoService.java
package com.sushiroom.backend.services;

import com.sushiroom.backend.models.Producto;
import com.sushiroom.backend.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    
    public Producto save(Producto producto) {
        if (producto.getFechaCreacion() == null) {
            producto.setFechaCreacion(LocalDateTime.now());
        }
        return productoRepository.save(producto);
    }
    
    public void deleteById(Integer id) {
        productoRepository.deleteById(id);
    }
    
    public List<Producto> findByCategoriaId(Integer categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }
}