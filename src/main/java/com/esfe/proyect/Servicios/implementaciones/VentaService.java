package com.esfe.proyect.Servicios.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esfe.proyect.Modelos.DetalleVenta;
import com.esfe.proyect.Modelos.Venta;
import com.esfe.proyect.Repositorios.IDetalleVentaRepository;
import com.esfe.proyect.Repositorios.IVentaRepository;
import com.esfe.proyect.Servicios.interfaces.IVentaService;

@Service
@Transactional
public class VentaService implements IVentaService {

    @Autowired
    private IVentaRepository ventaRepository;

    @Autowired
    private IDetalleVentaRepository detalleVentaRepository;

    @Override
    public Page<Venta> buscarTodos(Pageable pageable){
        return ventaRepository.findAll(pageable);
    }

    @Override
    public List<Venta> obtenerTodos(){
        return ventaRepository.findAll();
    } 

    @Override
    @Transactional
    public Optional<Venta> buscarPorId(Integer id) {
        return ventaRepository.findById(id);
    }

   @Override
   @Transactional
   public Venta crearOEditar(Venta venta) {
    System.out.println("Guardando venta: " + venta);
    System.out.println("Detalles: " + (venta.getDetalles() != null ? venta.getDetalles().size() : 0));
    
    Venta ventaGuardada = ventaRepository.save(venta);
    System.out.println("Venta guardada con ID: " + ventaGuardada.getId());
    
    if (venta.getDetalles() != null) {
        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.setVenta(ventaGuardada);
            DetalleVenta detalleGuardado = detalleVentaRepository.save(detalle);
            System.out.println("Detalle guardado: " + detalleGuardado.getId());
        }
    }
    
    return ventaGuardada; 
}

    @Override 
    public void eliminarPorId(Integer id) {
        Optional<Venta> ventaOpt = ventaRepository.findById(id);
        if (ventaOpt.isPresent()) {
            Venta venta = ventaOpt.get();
            if (venta.getDetalles() != null) {
                detalleVentaRepository.deleteAll(venta.getDetalles());
            }
        }
        ventaRepository.deleteById(id);
    }

    @Override
    public List<Venta> buscarPorEstado(Venta.EstadoVenta estado) {
        return ventaRepository.findByEstado(estado);
    }
    
}
