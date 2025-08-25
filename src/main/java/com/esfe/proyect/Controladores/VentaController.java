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

import com.esfe.proyect.Modelos.Cliente;
import com.esfe.proyect.Modelos.Venta;
import com.esfe.proyect.Servicios.interfaces.IClienteService;
import com.esfe.proyect.Servicios.interfaces.IVentaService;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private IVentaService ventaService;

    @Autowired
    private IClienteService clienteService;

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
    public String create(Model model) {
        model.addAttribute("venta", new Venta());
        model.addAttribute("cliente", clienteService.obtenerTodos());
        model.addAttribute("action", "create");
        return "venta/mant";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Venta venta = ventaService.buscarPorId(id).orElseThrow();
        model.addAttribute("venta", venta);
        model.addAttribute("cliente", clienteService.obtenerTodos());
        model.addAttribute("action", "edit");
        return "venta/mant";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Integer id, Model model) {
        Venta venta = ventaService.buscarPorId(id).orElseThrow();
        model.addAttribute("venta", venta);
        model.addAttribute("cliente", clienteService.obtenerTodos());
        model.addAttribute("action", "view");
        return "venta/mant";
    }

    @GetMapping("/delete/{id}")
    public String deleteConfirm(@PathVariable Integer id, Model model) {
        Venta venta = ventaService.buscarPorId(id).orElseThrow();
        model.addAttribute("venta", venta);
        model.addAttribute("cliente", clienteService.obtenerTodos());
        model.addAttribute("action", "delete");
        return "venta/mant";
    }

    @PostMapping("/create")
    public String saveNuevo(@ModelAttribute Venta venta,
                            BindingResult result,
                            RedirectAttributes redirect,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("action", "create");
            model.addAttribute("cliente", clienteService.obtenerTodos());
            return "venta/mant";
        }
        ventaService.crearOEditar(venta);
        redirect.addFlashAttribute("msg", "Venta creada correctamente");
        return "redirect:/ventas";
    }

    @PostMapping("/edit")
    public String saveEditado(@ModelAttribute Venta venta,
                              BindingResult result,
                              RedirectAttributes redirect,
                              Model model) {
        if(result.hasErrors()) {
            model.addAttribute("action", "edit");
            model.addAttribute("cliente", clienteService.obtenerTodos());
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

}
