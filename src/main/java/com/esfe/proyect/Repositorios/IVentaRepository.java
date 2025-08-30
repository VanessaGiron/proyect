package com.esfe.proyect.Repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.esfe.proyect.Modelos.Venta;

public interface IVentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findByEstado(Venta.EstadoVenta estado);
    @Query("SELECT v FROM Venta v LEFT JOIN FETCH v.detalles WHERE v.id = :id")
    Optional<Venta> findByIdWithDetalles(@Param("id") Integer id);

}
