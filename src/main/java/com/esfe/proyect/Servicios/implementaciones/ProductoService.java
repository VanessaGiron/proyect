package com.esfe.proyect.Servicios.implementaciones;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.esfe.proyect.Modelos.Producto;
import com.esfe.proyect.Repositorios.IProductoRepository;
import com.esfe.proyect.Servicios.interfaces.IProductoService;

@Service
public class ProductoService implements IProductoService {

    @Autowired
    private IProductoRepository productoRepository;

    @Override
    public Page<Producto> buscarTodos(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    @Override
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    @Override
    public Producto crearOeditar(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public void eliminarPorId(Integer id) {
        productoRepository.deleteById(id);
    }
    public List<Producto> buscarProductosPorClienteYFecha(Integer clienteId, Date fecha) {
        return productoRepository.findProductosByClienteAndFecha(clienteId, fecha);
    }

    @Override
    public List<Producto> buscarPorIds(List<Integer> ids) {
        return productoRepository.findAllById(ids);
    }
    

}
