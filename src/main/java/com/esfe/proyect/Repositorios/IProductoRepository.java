package com.esfe.proyect.Repositorios;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.esfe.proyect.Modelos.Producto;

public interface IProductoRepository extends JpaRepository<Producto, Integer> {

     @Query("SELECT DISTINCT dv.producto FROM DetalleVenta dv " +
           "WHERE dv.venta.cliente.id = :clienteId " +
           "AND DATE(dv.venta.fechaVenta) = DATE(:fecha)")
    List<Producto> findProductosByClienteAndFecha(@Param("clienteId") Integer clienteId, 
                                                 @Param("fecha") Date fecha);

}
