package com.sushiroom.backend.repositories;

import com.sushiroom.backend.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUsuarioId(Integer usuarioId);

    List<Pedido> findByEstado(String estado);
    
    List<Pedido> findByEstadoOrderByFechaPedidoDesc(String estado);
    
    List<Pedido> findAllByOrderByFechaPedidoDesc();
    
    int countByEstado(String estado);
    
    int countByFechaPedidoBetween(LocalDateTime inicio, LocalDateTime fin);
    
    Optional<Pedido> findByNumeroPedido(String numeroPedido);
}