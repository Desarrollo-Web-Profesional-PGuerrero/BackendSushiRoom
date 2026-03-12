package com.sushiroom.backend.repositories;

import com.sushiroom.backend.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    // Buscar categoría por nombre (exacto)
    Optional<Categoria> findByNombre(String nombre);

    // Buscar categorías que contengan cierto texto en el nombre
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);

    // Verificar si existe una categoría por nombre
    boolean existsByNombre(String nombre);

    // Obtener todas las categorías con su cantidad de productos
    @Query("SELECT c, COUNT(p) FROM Categoria c LEFT JOIN c.productos p GROUP BY c")
    List<Object[]> findCategoriasWithProductCount();

    // Obtener solo categorías que tienen productos activos
    @Query("SELECT DISTINCT c FROM Categoria c JOIN c.productos p WHERE p.activo = true")
    List<Categoria> findCategoriasWithProductosActivos();

    // Ordenar por nombre
    List<Categoria> findAllByOrderByNombreAsc();
}