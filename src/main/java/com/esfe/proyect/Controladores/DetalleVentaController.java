package com.esfe.proyect.Controladores;

import java.util.List;
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
import com.esfe.proyect.Servicios.interfaces.IDestalleVentaService;
import com.esfe.proyect.Servicios.interfaces.IVentaService;

@Controller
@RequestMapping("/detalleVentas")
public class DetalleVentaController {

    @Autowired
    private IDestalleVentaService destalleVentaService;

    @Autowired
    private IVentaService ventaService;

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
        return "detalleVentas/index";
    }

    //---- CREAR -----
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("detalleVenta", new DetalleVenta());
        model.addAttribute("ventas", ventaService.obtenerTodos());
        model.addAttribute("action", "create");
        return "detalleVenta/mant";
    }

    //---- EDITAR -----
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        DetalleVenta detalleVenta = destalleVentaService.buscarPorId(id);
        model.addAttribute("detalleVenta", detalleVenta);
        model.addAttribute("ventas", ventaService.obtenerTodos());
        model.addAttribute("action", "edit");
        return "detalleVenta/mant";
    }

    //----- VER -----
    @GetMapping("/view/{id}")
    public String view(@PathVariable Integer id, Model model) {
        DetalleVenta detalleVenta = destalleVentaService.buscarPorId(id);
        model.addAttribute("detalleVenta", detalleVenta);
        model.addAttribute("ventas", ventaService.obtenerTodos());
        model.addAttribute("action", "view");
        return "detalleVenta/mant";
    }

    //----- ELIMINAR -----
    @GetMapping("/delete/{id}")
    public String deleteConfirm(@PathVariable Integer id, Model model) {
        DetalleVenta detalleVenta = destalleVentaService.buscarPorId(id);
        model.addAttribute("detalleVenta", detalleVenta);
        model.addAttribute("ventas", ventaService.obtenerTodos());
        model.addAttribute("action", "delete");
        return "detalleVenta/mant";
    }

    //---- PROCESAR POST ACTION ----
    @PostMapping("/create")
    public String saveNuevo(@ModelAttribute DetalleVenta detalleVenta, BindingResult result,
                            RedirectAttributes redirect, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("detalleVentas", destalleVentaService.obtenerTodos());
            model.addAttribute("ventas", ventaService.obtenerTodos());
            model.addAttribute("action", "create");
            return "detalleVenta/mant";
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
        model.addAttribute("ventas", ventaService.obtenerTodos());
        model.addAttribute("action", "edit");
        return "detalleVenta/mant";
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
}
