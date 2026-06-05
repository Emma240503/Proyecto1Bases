package com.example.proyecto1bases.controller;

import com.example.proyecto1bases.model.Partido;
import com.example.proyecto1bases.model.Quiniela;
import com.example.proyecto1bases.model.Usuario;
import com.example.proyecto1bases.service.PartidoService;
import com.example.proyecto1bases.service.QuinielaService;
import com.example.proyecto1bases.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class QuinielaController {

    @Autowired
    private QuinielaService quinielaService;

    @Autowired
    private PartidoService partidoService;

    @Autowired
    private UsuarioService usuarioService;



    @GetMapping("/admin/quinielas")
    public String listaAdmin(Model model) {
        model.addAttribute("quinielas", quinielaService.obtenerTodas());
        return "quinielas/lista";
    }

    @GetMapping("/admin/quinielas/nueva")
    public String nuevaForm(Model model) {
        model.addAttribute("quiniela", new Quiniela());
        model.addAttribute("accion", "nueva");
        return "quinielas/form";
    }

    @PostMapping("/admin/quinielas")
    public String crear(@ModelAttribute Quiniela quiniela, RedirectAttributes redirect) {
        try {
            if (quiniela.getEstado() == null || quiniela.getEstado().isBlank()) {
                quiniela.setEstado("ABIERTA");
            }
            quinielaService.insertarQuiniela(quiniela);
            redirect.addFlashAttribute("mensaje", "Quiniela creada exitosamente.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al crear la quiniela: " + e.getMessage());
        }
        return "redirect:/admin/quinielas";
    }

    @GetMapping("/admin/quinielas/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        Quiniela quiniela = quinielaService.obtenerPorId(id);
        model.addAttribute("quiniela", quiniela);
        model.addAttribute("accion", "editar");
        return "quinielas/form";
    }

    @PostMapping("/admin/quinielas/{id}")
    public String actualizar(@PathVariable Long id,
                              @ModelAttribute Quiniela quiniela,
                              RedirectAttributes redirect) {
        try {
            quiniela.setIdQuiniela(id);
            quinielaService.actualizarQuiniela(quiniela);
            redirect.addFlashAttribute("mensaje", "Quiniela actualizada.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/admin/quinielas";
    }

    @PostMapping("/admin/quinielas/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                 @RequestParam String estado,
                                 RedirectAttributes redirect) {
        quinielaService.cambiarEstado(id, estado);
        redirect.addFlashAttribute("mensaje", "Estado cambiado a: " + estado);
        return "redirect:/admin/quinielas";
    }

    @PostMapping("/admin/quinielas/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            quinielaService.eliminarQuiniela(id);
            redirect.addFlashAttribute("mensaje", "Quiniela eliminada.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin/quinielas";
    }

    /** Detalle de quiniela para ADMIN: muestra partidos con botones de edición. */
    @GetMapping("/admin/quinielas/{id}")
    public String detalleAdmin(@PathVariable Long id, Model model) {
        Quiniela quiniela = quinielaService.obtenerPorId(id);
        List<Partido> partidos = partidoService.obtenerPorQuiniela(id);
        model.addAttribute("quiniela", quiniela);
        model.addAttribute("partidos", partidos);
        model.addAttribute("esAdmin", true);
        return "quinielas/detalle";
    }

    // JUGADOR — Ver quinielas e inscribirse


    @GetMapping("/jugador/quinielas")
    public String listaJugador(Model model) {
        model.addAttribute("quinielas", quinielaService.obtenerTodas());
        return "quinielas/lista";
    }

    @PostMapping("/jugador/quinielas/{id}/inscribir")
    public String inscribir(@PathVariable Long id,
                             @RequestParam(defaultValue = "true") boolean aceptoReglas,
                             Authentication auth,
                             RedirectAttributes redirect) {
        try {
            Usuario usuario = usuarioService.obtenerPorUsername(auth.getName());
            quinielaService.inscribirUsuario(usuario.getIdUsuario(), id, aceptoReglas);
            redirect.addFlashAttribute("mensaje", "¡Inscripción exitosa!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al inscribirse: " + e.getMessage());
        }
        return "redirect:/jugador/quinielas";
    }

    @GetMapping("/jugador/quinielas/{id}")
    public String detalleJugador(@PathVariable Long id, Model model, Authentication auth) {
        Quiniela quiniela = quinielaService.obtenerPorId(id);
        List<Partido> partidos = partidoService.obtenerPorQuiniela(id);
        model.addAttribute("quiniela", quiniela);
        model.addAttribute("partidos", partidos);
        model.addAttribute("esAdmin", false);
        return "quinielas/detalle";
    }
}
