package com.esfe.proyect.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Producto {
//seria como inventario de productos(sistemas) que se vendenrian 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre del producto es requerido")
    private String nombre;

    @NotBlank(message = "La descripción es requerida")
    private String descripcion;

    @NotNull(message = "El precio es requerido")
    @Positive(message = "El precio debe ser mayor que cero")
    private Double precio;

    @NotNull(message = "La cantidad en stock es requerida")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer stock;

    @NotBlank(message = "El tipo de producto(sistema) es requerido")
    private String tipo;

    @NotBlank(message = "La marca es requerida")
    private String marca;


    public Producto() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }


}
