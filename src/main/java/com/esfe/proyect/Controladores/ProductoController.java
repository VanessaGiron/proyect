package com.esfe.proyect.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;

import com.esfe.proyect.Modelos.Producto;
import com.esfe.proyect.Servicios.interfaces.IProductoService;
import com.esfe.proyect.utilidades.PdfGeneratorService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    private IProductoService productoService;
    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping
    public String index (Model model,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        
        Page<Producto> productos = productoService.buscarTodos(pageable);
        model.addAttribute("productos", productos);

        int totalPages = productos.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        
        return "producto/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("action", "create");
        return "producto/mant";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Producto producto = productoService.buscarPorId(id).orElseThrow();
        model.addAttribute("producto", producto);
        model.addAttribute("action", "edit");
        return "producto/mant";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Integer id, Model model) {
        Producto producto = productoService.buscarPorId(id).orElseThrow();
        model.addAttribute("producto", producto);
        model.addAttribute("action", "view");
        return "producto/mant";
    }

    @GetMapping("/delete/{id}")
    public String deleteConfirm(@PathVariable Integer id, Model model) {
        Producto producto = productoService.buscarPorId(id).orElseThrow();
        model.addAttribute("producto", producto);
        model.addAttribute("action", "delete");
        return "producto/mant";
    }

    @PostMapping("/create")
    public String saveNuevo(@ModelAttribute Producto producto, 
                          BindingResult result, 
                          RedirectAttributes redirect, 
                          Model model) {
        if(result.hasErrors()) {
            model.addAttribute("action", "create");
            return "producto/mant";
        }
        productoService.crearOeditar(producto);
        redirect.addFlashAttribute("msg", "Producto creado correctamente");
        return "redirect:/producto";
    }

    @PostMapping("/edit")
    public String saveEditado(@ModelAttribute Producto producto, 
                            BindingResult result,
                            RedirectAttributes redirect, 
                            Model model) {
        if(result.hasErrors()) {
            model.addAttribute("action", "edit");
            return "producto/mant";
        }
        productoService.crearOeditar(producto);
        redirect.addFlashAttribute("msg", "Producto actualizado correctamente");
        return "redirect:/producto";
    }

    @PostMapping("/delete")
    public String deleteProductos(@ModelAttribute Producto producto, 
                              RedirectAttributes redirect) {
        productoService.eliminarPorId(producto.getId());
        redirect.addFlashAttribute("msg", "Producto eliminado correctamente");
        return "redirect:/producto";
    }

    @GetMapping("/productoPDF")
public void generarPdfProductos(Model model, HttpServletResponse response) throws Exception {
    // 1. Obtener datos de productos a mostrar en el PDF
    List<Producto> productos = productoService.obtenerTodos(); // O tu método que devuelve todos los productos

    // 2. Preparar datos para Thymeleaf
    Map<String, Object> data = new HashMap<>();
    data.put("productos", productos);

    // 3. Generar PDF (nombre de la plantilla Thymeleaf para productos)
    byte[] pdfBytes = pdfGeneratorService.generatePdfReport("producto/RPProducto", data);

    // 4. Configurar la respuesta HTTP para descargar o mostrar el PDF
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "inline; filename=productos.pdf");
    response.setContentLength(pdfBytes.length);

    // 5. Escribir el PDF en la respuesta
    response.getOutputStream().write(pdfBytes);
    response.getOutputStream().flush();
}



}
