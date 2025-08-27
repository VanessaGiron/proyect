package com.esfe.proyect.Controladores;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.esfe.proyect.Modelos.Rol;
import com.esfe.proyect.Modelos.Usuario;
import com.esfe.proyect.Servicios.interfaces.IRolService;
import com.esfe.proyect.Servicios.interfaces.IUsuarioService;


@Controller
@RequestMapping("/usuarios")

public class UsuarioController {
    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IRolService rolService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping
    public String index(Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1) - 1; //falta -1
        int pageSize = size.orElse(5);

        Pageable pageable = PageRequest.of(currentPage , pageSize); //aqui estaba un -1 en medio

        Page<Usuario> usuarios = usuarioService.buscarTodos(pageable);
        model.addAttribute("usuarios", usuarios);

        int totalPages = usuarios.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "usuario/index";
    }

    @GetMapping("/create")
    public String create(Usuario usuario, Model model) {
        model.addAttribute("roles", rolService.obtenerTodos());
        return "usuario/create";
    }

    @PostMapping("/save")
    public String save(@RequestParam("rol") Integer rol, Usuario usuario, BindingResult result, Model model,
            RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute(usuario);
            model.addAttribute("roles", rolService.obtenerTodos());
            attributes.addFlashAttribute("error", "Error al guardar el usuario");
            return "usuario/create";
        }

        String password = passwordEncoder.encode(usuario.getClave());
        Rol perfil = new Rol();
        perfil.setId(rol);

        usuario.setStatus(1);
        usuario.agregar(perfil);
        usuario.setClave(password);
        usuarioService.crearOeditar(usuario);
        attributes.addFlashAttribute("msg", "Usuario creado con exito");
        return "redirect:/usuarios";
    }

@GetMapping("/remove/{id}")
public String eliminar(@PathVariable("id") Integer id, RedirectAttributes attributes) {
    try {
        usuarioService.eliminarPorId(id); // llama al método de la interfaz
        attributes.addFlashAttribute("msg", "Usuario eliminado con éxito");
    } catch (Exception e) {
        attributes.addFlashAttribute("error", "No se pudo eliminar el usuario");
    }
    return "redirect:/usuarios";
}

@GetMapping("/detalle/{id}")
public String detalle(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
    Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
    if (usuarioOpt.isPresent()) {
        model.addAttribute("usuario", usuarioOpt.get());
        return "usuario/detalle"; // Vista solo lectura
    } else {
        attributes.addFlashAttribute("error", "Usuario no encontrado");
        return "redirect:/usuarios";
    }
}



}
