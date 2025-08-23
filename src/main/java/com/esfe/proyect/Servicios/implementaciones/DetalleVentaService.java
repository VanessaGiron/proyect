package com.esfe.proyect.Servicios.implementaciones;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.esfe.proyect.Modelos.DetalleVenta;
import com.esfe.proyect.Repositorios.IDetalleVentaRepository;
import com.esfe.proyect.Servicios.interfaces.IDestalleVentaService;

@Service
public class DetalleVentaService implements IDestalleVentaService {
    @Autowired
    private IDetalleVentaRepository detalleVentaRepository;
    @Override
    public List<DetalleVenta> obtenerTodos() {
        return detalleVentaRepository.findAll();
    }

    @Override
    public Page<DetalleVenta> buscarTodosPaginados(Pageable pageable) {
        return detalleVentaRepository.findByOrderByDetalleVentaDesc(pageable);
    }

    @Override
    public DetalleVenta buscarPorId(Integer id) {
        return detalleVentaRepository.findById(id).get();
    }

    @Override
    public DetalleVenta crearOEditar(DetalleVenta detalleVenta) {
        return detalleVentaRepository.save(detalleVenta);
    }

    @Override 
    public void eliminarPorId(Integer id) {
        detalleVentaRepository.deleteById(id);
    }

}
