package com.esfe.proyect.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.esfe.proyect.Modelos.Producto;

public interface IProductoRepository extends JpaRepository<Producto, Integer> {

}
