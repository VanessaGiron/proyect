package com.esfe.proyect.Servicios.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.esfe.proyect.Modelos.Venta;
import com.esfe.proyect.Repositorios.IVentaRepository;
import com.esfe.proyect.Servicios.interfaces.IVentaService;

@Service
public class VentaService implements IVentaService {

    @Autowired
    private IVentaRepository ventaRepository;

    @Override
    public Page<Venta> buscarTodos(Pageable pageable){
        return ventaRepository.findAll(pageable);
    }

    @Override
    public List<Venta> obtenerTodos(){
        return ventaRepository.findAll();
    } 

    @Override
    public Optional<Venta> buscarPorId(Integer id) {
        return ventaRepository.findById(id);
    }

    @Override
    public Venta crearOEditar(Venta venta) {
        return ventaRepository.save(venta);
    }

    @Override 
    public void eliminarPorId(Integer id) {
        ventaRepository.deleteById(id);;
    }
}
