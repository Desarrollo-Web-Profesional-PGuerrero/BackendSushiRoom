package com.sushiroom.backend.repositories;

import com.sushiroom.backend.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByActivoTrue();

    List<Producto> findByCategoriaId(Integer categoriaId);

    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.categoria.nombre, p.nombre")
    List<Producto> findMenuActivo();

    @Query(value = "SELECT * FROM vista_menu_activo", nativeQuery = true)
    List<Object[]> findMenuActivoView();
}