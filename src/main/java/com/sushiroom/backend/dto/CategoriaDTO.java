// src/main/java/com/sushiroom/backend/dto/CategoriaDTO.java
package com.sushiroom.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoriaDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private List<ProductoDTO> productos;
}