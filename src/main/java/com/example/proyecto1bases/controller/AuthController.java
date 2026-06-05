package com.example.proyecto1bases.controller;

import com.example.proyecto1bases.model.Usuario;
import com.example.proyecto1bases.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario, RedirectAttributes redirect) {
        try {
            usuarioService.registrarUsuario(usuario);
            redirect.addFlashAttribute("mensaje", "¡Registro exitoso! Inicia sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
            return "redirect:/registro";
        }
    }


    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        boolean isAdmin = auth.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
        model.addAttribute("username", auth.getName());
        model.addAttribute("isAdmin", isAdmin);
        return "dashboard";
    }
}
