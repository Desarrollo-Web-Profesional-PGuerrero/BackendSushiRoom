package com.sushiroom.backend.repositories;

import com.sushiroom.backend.models.Carrito;
import com.sushiroom.backend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

    List<Carrito> findByUsuarioId(Integer usuarioId);

    Optional<Carrito> findByUsuarioIdAndProductoId(Integer usuarioId, Integer productoId);

    void deleteByUsuarioId(Integer usuarioId);

    @Query("SELECT SUM(c.cantidad * p.precio) FROM Carrito c JOIN c.producto p WHERE c.usuario = :usuario")
    Double calcularTotalByUsuario(@Param("usuario") Usuario usuario);
}