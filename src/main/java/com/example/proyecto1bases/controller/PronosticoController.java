package com.example.proyecto1bases.controller;

import com.example.proyecto1bases.model.Partido;
import com.example.proyecto1bases.model.Quiniela;
import com.example.proyecto1bases.model.Usuario;
import com.example.proyecto1bases.service.PartidoService;
import com.example.proyecto1bases.service.PronosticoService;
import com.example.proyecto1bases.service.QuinielaService;
import com.example.proyecto1bases.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class PronosticoController {

    @Autowired
    private PronosticoService pronosticoService;

    @Autowired
    private PartidoService partidoService;

    @Autowired
    private QuinielaService quinielaService;

    @Autowired
    private UsuarioService usuarioService;


    /** Muestra el formulario de pronóstico para un partido de una quiniela. */
    @GetMapping("/jugador/pronosticos/{quinielaId}/{partidoId}")
    public String formulario(@PathVariable Long quinielaId,
                              @PathVariable Long partidoId,
                              Model model) {
        Partido partido = partidoService.obtenerPorId(partidoId);
        Quiniela quiniela = quinielaService.obtenerPorId(quinielaId);

        if (partido == null || quiniela == null) {
            return "redirect:/jugador/quinielas";
        }
        if (!"PENDIENTE".equals(partido.getEstado())) {
            return "redirect:/jugador/quinielas/" + quinielaId + "?errorPartidoNoDisponible=true";
        }

        model.addAttribute("partido", partido);
        model.addAttribute("quiniela", quiniela);
        return "pronosticos/form";
    }

    /** Guarda el pronóstico del jugador. */
    @PostMapping("/jugador/pronosticos")
    public String guardar(@RequestParam Long quinielaId,
                           @RequestParam Long partidoId,
                           @RequestParam Integer golesLocal,
                           @RequestParam Integer golesVisitante,
                           Authentication auth,
                           RedirectAttributes redirect) {
        try {
            Usuario usuario = usuarioService.obtenerPorUsername(auth.getName());
            pronosticoService.insertarPronostico(
                    usuario.getIdUsuario(), partidoId, quinielaId,
                    golesLocal, golesVisitante
            );
            redirect.addFlashAttribute("mensaje", "¡Pronóstico guardado exitosamente!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al guardar pronóstico: " + e.getMessage());
        }
        return "redirect:/jugador/quinielas/" + quinielaId;
    }

    // ═══════════════════════════════════════════════════════════════
    // RANKING — Accesible para todos los usuarios autenticados
    // ═══════════════════════════════════════════════════════════════

    @GetMapping("/ranking/{quinielaId}")
    public String ranking(@PathVariable Long quinielaId, Model model) {
        Quiniela quiniela = quinielaService.obtenerPorId(quinielaId);
        List<Map<String, Object>> ranking = pronosticoService.obtenerRanking(quinielaId);
        model.addAttribute("quiniela", quiniela);
        model.addAttribute("ranking", ranking);
        return "ranking";
    }
}
