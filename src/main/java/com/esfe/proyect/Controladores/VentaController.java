package com.esfe.proyect.Controladores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.esfe.proyect.Modelos.Cliente;
import com.esfe.proyect.Modelos.DetalleVenta;
import com.esfe.proyect.Modelos.Producto;
import com.esfe.proyect.Modelos.Venta;
import com.esfe.proyect.Servicios.interfaces.IClienteService;
import com.esfe.proyect.Servicios.interfaces.IProductoService;
import com.esfe.proyect.Servicios.interfaces.IVentaService;
import com.esfe.proyect.utilidades.PdfGeneratorService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/ventas")
@SessionAttributes("venta")
public class VentaController {

    @Autowired
    private IVentaService ventaService;

    @Autowired
    private IClienteService clienteService;

    @Autowired
    private IProductoService productoService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping
    public String index(Model model,
                    @RequestParam("page") Optional<Integer> page,
                    @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
       
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        Page<Venta> ventas = ventaService.buscarTodos(pageable);
        model.addAttribute("ventas", ventas);

        int totalPages = ventas.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
            .boxed().collect(Collectors.toList());
        model.addAttribute("pageNumbers", pageNumbers);
        }
        return "venta/index";
    }

    // tengo que agregar los clientes
    @GetMapping("/create")
public String create(Model model, HttpSession session) {
    
    // Verificar si ya hay una venta en sesión
    Venta venta = (Venta) session.getAttribute("venta");
    
    if (venta == null) {
        venta = new Venta();                    
        venta.setEstado(Venta.EstadoVenta.ACTIVA);
        venta.setFechaVenta(LocalDate.now()); 
        venta.setDetalles(new ArrayList<>());
        session.setAttribute("venta", venta);
        System.out.println("Nueva venta creada en sesión");
    } else {
        System.out.println("Venta existente en sesión con " + 
                          (venta.getDetalles() != null ? venta.getDetalles().size() : 0) + 
                          " detalles");
    }
    
    model.addAttribute("venta", venta);
    model.addAttribute("clientes", clienteService.obtenerTodos());
    model.addAttribute("productos", productoService.obtenerTodos());
    model.addAttribute("detalleTemporal", new DetalleVenta());
    model.addAttribute("action", "create");
    
    return "venta/mant-unificado";
}

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Venta venta = ventaService.buscarPorId(id).orElseThrow();
        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("productos", productoService.obtenerTodos());
        model.addAttribute("action", "edit");
        return "venta/mant";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Integer id, Model model) {
        Venta venta = ventaService.buscarPorId(id).orElseThrow();
        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("productos", productoService.obtenerTodos());
        model.addAttribute("action", "view");
        return "venta/mant";
    }

    @GetMapping("/delete/{id}")
    public String deleteConfirm(@PathVariable Integer id, Model model) {
        Venta venta = ventaService.buscarPorId(id).orElseThrow();
        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("productos", productoService.obtenerTodos());
        model.addAttribute("action", "delete");
        return "venta/mant";
    }


    @PostMapping("/create")
public String saveNuevo(@ModelAttribute("venta") Venta venta,
                       BindingResult result,
                       RedirectAttributes redirectAttributes,
                       Model model,
                       HttpSession session,
                       SessionStatus status) {
    
    if (result.hasErrors()) {
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("productos", productoService.obtenerTodos());
        model.addAttribute("detalleTemporal", new DetalleVenta());
        model.addAttribute("action", "create");
        return "venta/mant-unificado";
    }
    
    // Validar que haya detalles
    if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("productos", productoService.obtenerTodos());
        model.addAttribute("detalleTemporal", new DetalleVenta());
        model.addAttribute("action", "create");
        model.addAttribute("error", "Debe agregar al menos un producto a la venta");
        return "venta/mant-unificado";
    }
    
    try {
        // Asegurar que el cliente esté completamente cargado
        if (venta.getCliente() != null && venta.getCliente().getId() != null) {
            Cliente clienteCompleto = clienteService.buscarPorId(venta.getCliente().getId())
                .orElseThrow();
            venta.setCliente(clienteCompleto);
        }
        
        // Guardar la venta
        ventaService.crearOEditar(venta);
        
        // Limpiar la sesión después de guardar exitosamente
        session.removeAttribute("venta");
        status.setComplete();
        
        redirectAttributes.addFlashAttribute("msg", "Venta creada correctamente");
        return "redirect:/ventas";
        
    } catch (Exception e) {
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("productos", productoService.obtenerTodos());
        model.addAttribute("detalleTemporal", new DetalleVenta());
        model.addAttribute("action", "create");
        model.addAttribute("error", "Error al guardar la venta: " + e.getMessage());
        return "venta/mant-unificado";
    }
}

    @PostMapping("/edit")
    public String saveEditado(@ModelAttribute Venta venta,@RequestParam(value = "productosSeleccionados", required = false) List<Integer> productosIds,
                              BindingResult result,
                              RedirectAttributes redirect,
                              Model model) {
        if(result.hasErrors()) {
            model.addAttribute("action", "edit");
            model.addAttribute("clientes", clienteService.obtenerTodos());
            return "venta/mant";
        }
        ventaService.crearOEditar(venta);
        redirect.addFlashAttribute("msg", "venta actualizada correctamente");
        return "redirect:/ventas";
    }
    

    @PostMapping("/delete")
    public String deleteVenta(@ModelAttribute Venta venta,
                              RedirectAttributes redirect) {
        ventaService.eliminarPorId(venta.getId());
        redirect.addFlashAttribute("msg", "venta eliminada correctamente");
        return "redirect:/ventas";
    }

     @GetMapping("/ventaPDF")
    public void generarPdf(Model model, HttpServletResponse response) throws Exception {
        // 1. Obtener datos a mostrar en el pdf
        List<Venta> ventas = ventaService.obtenerTodos();

        // 2. Preparar datos para Thymeleaf
        Map<String, Object> data = new HashMap<>();
        data.put("ventas", ventas);

        //3. Generar PDF (con el nombre de la plantilla Thymeleaf que quieres usar)
        byte[] pdfBytes = pdfGeneratorService.generatePdfReport("venta/RPVenta", data);

        // 4. Configurar la respuesta HTTP para descargar o mostrar el PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=ventas.pdf");
        response.setContentLength(pdfBytes.length);

        //5. Escribir el PDF en la respuesta
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

    @GetMapping("/anular/{id}")
    public String anular(@PathVariable Integer id) {
    Venta venta = ventaService.buscarPorId(id).orElseThrow();
    venta.setEstado(Venta.EstadoVenta.ANULADA); // cambiar estado
    ventaService.crearOEditar(venta); // guardar cambios
    return "redirect:/ventas";
    }
    @PostMapping("/agregarDetalle")
public String agregarDetalle(@ModelAttribute("venta") Venta venta,
                        @RequestParam("productoId") Integer productoId,
                        @RequestParam("cantidad") Integer cantidad,
                        @RequestParam("precioUnitario") Double precioUnitario,
                        RedirectAttributes redirectAttributes,
                        HttpSession session) {

    if (venta.getDetalles() == null) {
        venta.setDetalles(new ArrayList<>());
    }
    
    Optional<Producto> productoOpt = productoService.buscarPorId(productoId);
    if (productoOpt.isPresent()) {
        Producto producto = productoOpt.get();
        
        DetalleVenta nuevoDetalle = new DetalleVenta();
        nuevoDetalle.setProducto(producto);
        nuevoDetalle.setCantidad(cantidad);
        nuevoDetalle.setPrecioUnitario(precioUnitario);
        
        // Calcular subtotal correctamente
        BigDecimal subtotal = BigDecimal.valueOf(cantidad).multiply(BigDecimal.valueOf(precioUnitario));
        nuevoDetalle.setSubtotal(subtotal);
        
        venta.getDetalles().add(nuevoDetalle);
        
        // Recalcular total
        BigDecimal total = venta.getDetalles().stream()
            .map(DetalleVenta::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        venta.setTotal(total.doubleValue());
        
        session.setAttribute("venta", venta);
        redirectAttributes.addFlashAttribute("msg", "Producto agregado correctamente");
    } else {
        redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
    }
    
    return "redirect:/ventas/create";

}
    @GetMapping("/eliminarDetalle/{index}")
    public String eliminarDetalle(@ModelAttribute("venta") Venta venta,
                                 @PathVariable int index,
                                 RedirectAttributes redirectAttributes) {
        if (venta.getDetalles() != null && index >= 0 && index < venta.getDetalles().size()) {
            DetalleVenta detalleEliminado = venta.getDetalles().remove(index);
            
            double total = venta.getDetalles().stream()
                .mapToDouble(d -> d.getSubtotal().doubleValue())
                .sum();
            venta.setTotal(total);
            
            redirectAttributes.addFlashAttribute("msg", "Producto eliminado: " + detalleEliminado.getProducto().getNombre());
        } else {
            redirectAttributes.addFlashAttribute("error", "Índice de detalle inválido");
        }
        
        return "redirect:/ventas/create";
    }

}
