package com.esfe.proyect.Servicios.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.esfe.proyect.Modelos.DetalleVenta;

public interface IDestalleVentaService {

    List<DetalleVenta> obtenerTodos();

    Page<DetalleVenta> buscarTodosPaginados(Pageable pageable);

    DetalleVenta buscarPorId(Integer id);

    DetalleVenta crearOEditar(DetalleVenta detalleVenta);

    void eliminarPorId(Integer id);

}
