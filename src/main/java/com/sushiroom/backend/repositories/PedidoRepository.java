package com.sushiroom.backend.repositories;

import com.sushiroom.backend.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUsuarioId(Integer usuarioId);

    List<Pedido> findByEstado(String estado);

    @Query(value = "SELECT * FROM vista_pedidos_admin", nativeQuery = true)
    List<Object[]> findPedidosParaAdmin();
}