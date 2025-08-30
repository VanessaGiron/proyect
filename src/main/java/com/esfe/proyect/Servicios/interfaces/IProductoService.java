package com.esfe.proyect.Servicios.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.esfe.proyect.Modelos.Producto;

public interface IProductoService {

    Page<Producto> buscarTodos(Pageable pageable);

    List<Producto> obtenerTodos();

    Optional<Producto> buscarPorId(Integer id);

    Producto crearOeditar(Producto producto);

    void eliminarPorId(Integer id);

    List<Producto> buscarPorIds(List<Integer> ids);

}
