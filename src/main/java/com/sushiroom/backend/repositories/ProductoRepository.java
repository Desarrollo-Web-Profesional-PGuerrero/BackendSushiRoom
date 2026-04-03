// ProductoRepository.java
package com.sushiroom.backend.repositories;

import com.sushiroom.backend.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByActivoTrue();

    List<Producto> findByCategoriaId(Integer categoriaId);
}