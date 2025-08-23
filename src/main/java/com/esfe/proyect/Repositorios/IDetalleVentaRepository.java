package com.esfe.proyect.Repositorios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.esfe.proyect.Modelos.DetalleVenta;

public interface IDetalleVentaRepository extends JpaRepository<DetalleVenta,Integer> {

    Page<DetalleVenta> findByOrderByDetalleVentaDesc(Pageable pageable);
}
