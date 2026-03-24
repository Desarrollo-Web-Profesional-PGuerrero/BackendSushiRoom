// src/main/java/com/sushiroom/backend/dto/ProductoDTO.java
package com.sushiroom.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String origen;
    private String notasCata;
    private BigDecimal precio;
    private String imagenUrl;
    private Boolean activo;
    private Integer categoriaId;
    private String categoriaNombre;
}