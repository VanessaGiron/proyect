package com.esfe.proyect.Servicios.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.esfe.proyect.Modelos.Venta;

public interface IVentaService {

    Page<Venta> buscarTodos(Pageable pageable);

    List<Venta> obtenerTodos();

    Optional<Venta> buscarPorId(Integer id);

    Venta crearOEditar(Venta venta);

    void eliminarPorId(Integer id);

    List<Venta> buscarPorEstado(Venta.EstadoVenta estado);

}
