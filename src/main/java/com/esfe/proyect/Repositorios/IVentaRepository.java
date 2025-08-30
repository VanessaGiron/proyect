package com.esfe.proyect.Repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.esfe.proyect.Modelos.Venta;

public interface IVentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findByEstado(Venta.EstadoVenta estado);

}
