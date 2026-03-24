// ProductoService.java
package com.sushiroom.backend.services;

import com.sushiroom.backend.models.Producto;
import com.sushiroom.backend.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> findAllActivos() {
        return productoRepository.findByActivoTrue();
    }

    public Producto findById(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }
}