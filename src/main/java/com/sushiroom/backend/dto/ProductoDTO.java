// dto/ProductoDTO.java
package com.sushiroom.backend.dto;

import java.math.BigDecimal;

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
    
    // Constructores
    public ProductoDTO() {}
    
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
    
    public String getNotasCata() { return notasCata; }
    public void setNotasCata(String notasCata) { this.notasCata = notasCata; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }
    
    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }
}