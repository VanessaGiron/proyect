package com.esfe.proyect.Servicios.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

import com.esfe.proyect.Modelos.Cliente;

import java.util.List;

public interface IClienteService {



    Page<Cliente> buscarTodos(Pageable pageable);

    List<Cliente> obtenerTodos();

    Optional<Cliente> buscarPorId(Integer id);

    Cliente crearOeditar(Cliente cliente);

    void eliminarPorId(Integer id);



}
