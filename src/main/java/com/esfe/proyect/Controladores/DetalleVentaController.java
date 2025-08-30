/*package com.esfe.proyect.Controladores;

import java.math.BigDecimal;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.esfe.proyect.Modelos.DetalleVenta;
import com.esfe.proyect.Modelos.Producto;
import com.esfe.proyect.Modelos.Venta;
import com.esfe.proyect.Servicios.interfaces.IClienteService;
import com.esfe.proyect.Servicios.interfaces.IDestalleVentaService;
import com.esfe.proyect.Servicios.interfaces.IProductoService;
import com.esfe.proyect.Servicios.interfaces.IVentaService;
import com.esfe.proyect.utilidades.PdfGeneratorService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/detalleVentas")
public class DetalleVentaController {

    @Autowired
    private IDestalleVentaService destalleVentaService;

    @Autowired
    private IVentaService ventaService;

    @Autowired
    private IProductoService productoService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private IClienteService clienteService;

    @GetMapping
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<DetalleVenta> detalleVentas = destalleVentaService.buscarTodosPaginados(pageable);
        model.addAttribute("detalleVentas", detalleVentas);

        int totalPages = detalleVentas.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
            .boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "detalleVenta/index";
    }

    //---- CREAR -----
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("detalleVenta", new DetalleVenta());
        model.addAttribute("venta", ventaService.obtenerTodos());
        model.addAttribute("producto", productoService.obtenerTodos());
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("action", "create");
        return "detalleVenta/mant";
    }

    //---- EDITAR -----
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        DetalleVenta detalleVenta = destalleVentaService.buscarPorId(id);
        model.addAttribute("detalleVenta", detalleVenta);
        model.addAttribute("venta", ventaService.obtenerTodos());
        model.addAttribute("producto", productoService.obtenerTodos());
        model.addAttribute("action", "edit");
        return "detalleVenta/mant";
    }

    //----- VER -----
    @GetMapping("/view/{id}")
    public String view(@PathVariable Integer id, Model model) {
        DetalleVenta detalleVenta = destalleVentaService.buscarPorId(id);
        model.addAttribute("detalleVenta", detalleVenta);
        model.addAttribute("venta", ventaService.obtenerTodos());
        model.addAttribute("producto", productoService.obtenerTodos());
        model.addAttribute("action", "view");
        return "detalleVenta/mant";
    }

    //----- ELIMINAR -----
    @GetMapping("/delete/{id}")
    public String deleteConfirm(@PathVariable Integer id, Model model) {
        DetalleVenta detalleVenta = destalleVentaService.buscarPorId(id);
        model.addAttribute("detalleVenta", detalleVenta);
        model.addAttribute("venta", ventaService.obtenerTodos());
        model.addAttribute("producto", productoService.obtenerTodos());
        model.addAttribute("action", "delete");
        return "detalleVenta/mant";
    }

    //---- PROCESAR POST ACTION ----
    @PostMapping("/create")
    public String saveNuevo(@ModelAttribute DetalleVenta detalleVenta, BindingResult result,
                            RedirectAttributes redirect, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("detalleVentas", destalleVentaService.obtenerTodos());
            model.addAttribute("venta", ventaService.obtenerTodos());
            model.addAttribute("producto", productoService.obtenerTodos());
            model.addAttribute("action", "create");
            return "detalleVenta/mant";
    }

    // Convertir IDs en objetos completos usando Optional
    Optional<Venta> ventaOpt = ventaService.buscarPorId(detalleVenta.getVenta().getId());
    ventaOpt.ifPresent(detalleVenta::setVenta);
    Optional<Producto> productoOpt = productoService.buscarPorId(detalleVenta.getProducto().getId());
    productoOpt.ifPresent(detalleVenta::setProducto);

    // Calcular subtotal automáticamente
    if (detalleVenta.getCantidad() != null && detalleVenta.getPrecioUnitario() != null) {
        detalleVenta.setSubtotal(BigDecimal.valueOf(detalleVenta.getCantidad() * detalleVenta.getPrecioUnitario()));
    }

    destalleVentaService.crearOEditar(detalleVenta);
    redirect.addFlashAttribute("msg", "Detalle de venta creada correctamente");
    return "redirect:/detalleVentas";
}
@PostMapping("/edit")
public String saveEditado(@ModelAttribute DetalleVenta detalleVenta, BindingResult result,
                          RedirectAttributes redirect, Model model) {

    if (result.hasErrors()) {
        model.addAttribute("detalleVentas", destalleVentaService.obtenerTodos());
        model.addAttribute("venta", ventaService.obtenerTodos());
        model.addAttribute("producto", productoService.obtenerTodos());
        model.addAttribute("action", "edit");
        return "detalleVenta/mant";
    }
     // Convertir IDs en objetos completos usando Optional
    Optional<Venta> ventaOpt = ventaService.buscarPorId(detalleVenta.getVenta().getId());
    ventaOpt.ifPresent(detalleVenta::setVenta);
    Optional<Producto> productoOpt = productoService.buscarPorId(detalleVenta.getProducto().getId());
    productoOpt.ifPresent(detalleVenta::setProducto);

    // Calcular subtotal automáticamente
    if (detalleVenta.getCantidad() != null && detalleVenta.getPrecioUnitario() != null) {
        detalleVenta.setSubtotal(BigDecimal.valueOf(detalleVenta.getCantidad() * detalleVenta.getPrecioUnitario()));
    }
    
    destalleVentaService.crearOEditar(detalleVenta);
    redirect.addFlashAttribute("msg", "Detalle de venta actualizada correctamente");
    return "redirect:/detalleVentas";
}

    @PostMapping("/delete")
    public String deleteDetalleVenta(@ModelAttribute DetalleVenta detalleVenta, RedirectAttributes redirect) {
        destalleVentaService.eliminarPorId(detalleVenta.getId());
        redirect.addFlashAttribute("msg", "Detalle de venta eliminado correctamente");
        return "redirect:/detalleVentas";   
    }

     @GetMapping("/detallesPDF")
    public void generarPdfDetalles(Model model, HttpServletResponse response) throws Exception {
    // 1. Obtener datos de detalles de venta a mostrar en el PDF
    List<DetalleVenta> detalleVentas = destalleVentaService.obtenerTodos(); // O tu método que devuelve todos los detalles de venta

    // 2. Preparar datos para Thymeleaf
    Map<String, Object> data = new HashMap<>();
    data.put("detalleVentas", detalleVentas);

    // 3. Generar PDF (nombre de la plantilla Thymeleaf para detalles de venta)
    byte[] pdfBytes = pdfGeneratorService.generatePdfReport("detalleVenta/RPDetalles", data);

    // 4. Configurar la respuesta HTTP para descargar o mostrar el PDF
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "inline; filename=detalles.pdf");
    response.setContentLength(pdfBytes.length);

    // 5. Escribir el PDF en la respuesta
    response.getOutputStream().write(pdfBytes);
    response.getOutputStream().flush();
    }
}*/
