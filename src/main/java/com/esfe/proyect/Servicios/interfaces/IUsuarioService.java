package com.esfe.proyect.Servicios.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.esfe.proyect.Modelos.Usuario;

public interface IUsuarioService {
    Page<Usuario> buscarTodos(Pageable pageable);

    List<Usuario> obtenerTodos();

    Optional<Usuario> buscarPorId(Integer id);

    Usuario crearOeditar(Usuario usuario);

    void eliminarPorId(Integer id);
}
