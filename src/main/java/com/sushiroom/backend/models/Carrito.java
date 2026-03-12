package com.sushiroom.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "carrito", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "producto_id"})
})
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad = 1;

    @Column(name = "nivel_picante", length = 20)
    private String nivelPicante;

    @Column(name = "tipo_arroz", length = 50)
    private String tipoArroz;

    @Column(name = "notas_chef")
    private String notasChef;

    @Column(name = "fecha_agregado")
    private LocalDateTime fechaAgregado;
}