package com.esfe.proyect.Controladores;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.esfe.proyect.utilidades.PdfGeneratorService;
import com.esfe.proyect.Modelos.Cliente;
import com.esfe.proyect.Servicios.interfaces.IClienteService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/clientes")

public class ClienteController {

    @Autowired
    private IClienteService clienteService;

     @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping
    public String index(Model model,
                       @RequestParam("page") Optional<Integer> page,
                       @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        
        Page<Cliente> clientes = clienteService.buscarTodos(pageable);
        model.addAttribute("clientes", clientes);

        int totalPages = clientes.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "cliente/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("action", "create");
        return "cliente/mant";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id).orElseThrow();
        model.addAttribute("cliente", cliente);
        model.addAttribute("action", "edit");
        return "cliente/mant";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Integer id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id).orElseThrow();
        model.addAttribute("cliente", cliente);
        model.addAttribute("action", "view");
        return "cliente/mant";
    }

    @GetMapping("/delete/{id}")
    public String deleteConfirm(@PathVariable Integer id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id).orElseThrow();
        model.addAttribute("cliente", cliente);
        model.addAttribute("action", "delete");
        return "cliente/mant";
    }

    @PostMapping("/create")
    public String saveNuevo(@ModelAttribute Cliente cliente, 
                          BindingResult result, 
                          RedirectAttributes redirect, 
                          Model model) {
        if(result.hasErrors()) {
            model.addAttribute("action", "create");
            return "cliente/mant";
        }
        clienteService.crearOeditar(cliente);
        redirect.addFlashAttribute("msg", "Cliente creado correctamente");
        return "redirect:/clientes";
    }

    @PostMapping("/edit")
    public String saveEditado(@ModelAttribute Cliente cliente, 
                            BindingResult result,
                            RedirectAttributes redirect, 
                            Model model) {
        if(result.hasErrors()) {
            model.addAttribute("action", "edit");
            return "cliente/mant";
        }
        clienteService.crearOeditar(cliente);
        redirect.addFlashAttribute("msg", "Cliente actualizado correctamente");
        return "redirect:/clientes";
    }

    @PostMapping("/delete")
    public String deleteCliente(@ModelAttribute Cliente cliente, 
                              RedirectAttributes redirect) {
        clienteService.eliminarPorId(cliente.getId());
        redirect.addFlashAttribute("msg", "Cliente eliminado correctamente");
        return "redirect:/clientes";
    }
    @GetMapping("/clientePDF")
    public void generarPdf(Model model, HttpServletResponse response) throws Exception {
        // 1. Obtener datos a mostrar en el pdf
        List<Cliente> clientes = clienteService.obtenerTodos();

        // 2. Preparar datos para Thymeleaf
        Map<String, Object> data = new HashMap<>();
        data.put("clientes", clientes);

        //3. Generar PDF (con el nombre de la plantilla Thymeleaf que quieres usar)
        byte[] pdfBytes = pdfGeneratorService.generatePdfReport("cliente/RPCliente", data);

        // 4. Configurar la respuesta HTTP para descargar o mostrar el PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=clientes.pdf");
        response.setContentLength(pdfBytes.length);

        //5. Escribir el PDF en la respuesta
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }


}
