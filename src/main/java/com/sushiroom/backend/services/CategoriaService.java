// services/CategoriaService.java
package com.sushiroom.backend.services;

import com.sushiroom.backend.models.Categoria;
import com.sushiroom.backend.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CategoriaService {
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }
    
    public List<Categoria> findAllByOrderByNombreAsc() {
        return categoriaRepository.findAllByOrderByNombreAsc();
    }
    
    public Categoria findById(Integer id) {
        return categoriaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + id));
    }
    
    public Categoria findByNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + nombre));
    }
    
    @Transactional
    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }
    
    @Transactional
    public void deleteById(Integer id) {
        categoriaRepository.deleteById(id);
    }
    
    public boolean existsByNombre(String nombre) {
        return categoriaRepository.existsByNombre(nombre);
    }
    
    public List<Categoria> findCategoriasWithProductosActivos() {
        return categoriaRepository.findCategoriasWithProductosActivos();
    }
    
    // Método para inicializar categorías básicas al arrancar
    @Transactional
    public void inicializarCategoriasBasicas() {
        String[][] categoriasBasicas = {
            {"nigiri", "Nigiri - Arroz con pescado encima", "/images/categorias/nigiri.jpg"},
            {"roll", "Roll - Sushi enrollado", "/images/categorias/roll.jpg"},
            {"sashimi", "Sashimi - Pescado crudo sin arroz", "/images/categorias/sashimi.jpg"}
        };
        
        for (String[] cat : categoriasBasicas) {
            if (!existsByNombre(cat[0])) {
                Categoria categoria = new Categoria();
                categoria.setNombre(cat[0]);
                categoria.setDescripcion(cat[1]);
                categoria.setImagenUrl(cat[2]);
                categoriaRepository.save(categoria);
                System.out.println("✅ Categoría creada: " + cat[0]);
            }
        }
    }
}