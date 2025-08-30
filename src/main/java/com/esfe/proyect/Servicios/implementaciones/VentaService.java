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
    try {
        System.out.println("Guardando venta...");
        System.out.println("ID: " + venta.getId());
        System.out.println("Cliente: " + venta.getCliente().getId());
        System.out.println("Total: " + venta.getTotal());
        System.out.println("Número de detalles: " + (venta.getDetalles() != null ? venta.getDetalles().size() : 0));
        
        // Guardar la venta primero
        Venta ventaGuardada = ventaRepository.save(venta);
        System.out.println("Venta guardada con ID: " + ventaGuardada.getId());
        
        // Guardar los detalles
        if (venta.getDetalles() != null && !venta.getDetalles().isEmpty()) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                detalle.setVenta(ventaGuardada);
                System.out.println("Guardando detalle - Producto: " + detalle.getProducto().getId() + 
                                 ", Cantidad: " + detalle.getCantidad());
                detalleVentaRepository.save(detalle);
            }
            System.out.println("Detalles guardados exitosamente");
        }
        
        return ventaGuardada;
        
    } catch (Exception e) {
        System.out.println("Error en servicio al guardar venta: " + e.getMessage());
        e.printStackTrace();
        throw e;
    }
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
