package com.esfe.proyect.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Roles {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @NotBlank(message = "El nombre de rol es requerido")
    private String Nombre;

    public Integer getId(){
        return Id;
    }
    public void setId(Integer Id){
        this.Id = Id;
    }
    public String getNombre(){
        return Nombre;
    }
    public void setNombre(String Nombre){
        this.Nombre = Nombre;
    }

}
