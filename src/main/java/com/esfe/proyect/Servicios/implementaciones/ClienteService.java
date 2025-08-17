package com.esfe.proyect.Servicios.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.esfe.proyect.Modelos.Cliente;
import com.esfe.proyect.Repositorios.IClienteRepository;
import com.esfe.proyect.Servicios.interfaces.IClienteService;

@Service
public class ClienteService implements IClienteService {

    @Autowired
    private IClienteRepository clienteRepository;
    
    @Override
    public Page<Cliente> buscarTodos(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    @Override
    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Cliente crearOeditar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public void eliminarPorId(Integer id) {
        clienteRepository.deleteById(id);
    }
}