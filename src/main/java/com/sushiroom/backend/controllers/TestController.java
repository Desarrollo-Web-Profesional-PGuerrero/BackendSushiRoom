package com.sushiroom.backend.controllers;

import com.sushiroom.backend.repositories.ProductoRepository;
import com.sushiroom.backend.repositories.CategoriaRepository;
import com.sushiroom.backend.models.Categoria;
import com.sushiroom.backend.models.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping("/api/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "✅ SERVIDOR ACTIVO");
        response.put("mensaje", "El backend está corriendo correctamente");
        response.put("timestamp", new java.util.Date());
        return response;
    }

    @GetMapping("/api/test/productos")
    public Map<String, Object> testProductos() {
        Map<String, Object> response = new HashMap<>();
        try {
            long count = productoRepository.count();
            List<Producto> productos = productoRepository.findAll();

            response.put("status", "✅ CONEXIÓN A BD EXITOSA");
            response.put("total_productos", count);
            response.put("database", "Neon PostgreSQL");
            response.put("productos", productos);
        } catch (Exception e) {
            response.put("status", "❌ ERROR");
            response.put("error", e.getMessage());
        }
        return response;
    }

    @GetMapping("/api/test/categorias")
    public Map<String, Object> testCategorias() {
        Map<String, Object> response = new HashMap<>();
        try {
            long count = categoriaRepository.count();
            List<Categoria> categorias = categoriaRepository.findAllByOrderByNombreAsc();

            response.put("status", "✅ CATEGORIAS CARGADAS");
            response.put("total_categorias", count);

            // Crear un mapa con categorías y sus productos
            Map<String, Object> categoriasMap = new HashMap<>();
            for (Categoria cat : categorias) {
                categoriasMap.put(cat.getNombre(), cat.getProductos().size() + " productos");
            }

            response.put("categorias", categorias);
            response.put("resumen", categoriasMap);

        } catch (Exception e) {
            response.put("status", "❌ ERROR");
            response.put("error", e.getMessage());
        }
        return response;
    }

    @GetMapping("/api/test/all")
    public Map<String, Object> testCompleto() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Test Categorías
            long catCount = categoriaRepository.count();
            List<Categoria> categorias = categoriaRepository.findAll();

            // Test Productos
            long prodCount = productoRepository.count();
            List<Producto> productos = productoRepository.findAll();

            Map<String, Object> stats = new HashMap<>();
            stats.put("categorias", catCount);
            stats.put("productos", prodCount);

            // Verificar datos específicos de tu BD
            boolean tieneSake = productos.stream()
                    .anyMatch(p -> p.getNombre().toLowerCase().contains("sake"));

            response.put("status", "✅ TODO FUNCIONANDO");
            response.put("stats", stats);
            response.put("tiene_sake", tieneSake);
            response.put("primeras_categorias", categorias.stream().limit(3).toList());
            response.put("primeros_productos", productos.stream().limit(5).toList());

        } catch (Exception e) {
            response.put("status", "❌ ERROR");
            response.put("error", e.getMessage());
        }

        return response;
    }
}