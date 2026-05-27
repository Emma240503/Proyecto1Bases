package com.example.proyecto1bases.controller;

import com.example.proyecto1bases.model.Partido;
import com.example.proyecto1bases.model.Quiniela;
import com.example.proyecto1bases.service.PartidoService;
import com.example.proyecto1bases.service.QuinielaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/partidos")
public class PartidoController {

    @Autowired
    private PartidoService partidoService;

    @Autowired
    private QuinielaService quinielaService;

    /**
     * Lista de quinielas para seleccionar dónde administrar partidos.
     * No existe SP "ObtenerTodosLosPartidos", así que se navega
     * por quiniela → detalle con sus partidos.
     */
    @GetMapping
    public String lista(Model model) {
        model.addAttribute("quinielas", quinielaService.obtenerTodas());
        return "partidos/lista";
    }

    /** Formulario para crear un partido, recibe quinielaId como parámetro. */
    @GetMapping("/nuevo")
    public String nuevoForm(@RequestParam(required = false) Long quinielaId, Model model) {
        model.addAttribute("partido", new Partido());
        model.addAttribute("quinielas", quinielaService.obtenerTodas());
        model.addAttribute("quinielaIdSeleccionada", quinielaId);
        return "partidos/form";
    }

    /** Crea el partido y lo asocia a la quiniela seleccionada. */
    @PostMapping
    public String crear(@ModelAttribute Partido partido,
                         @RequestParam(required = false) Long quinielaId,
                         RedirectAttributes redirect) {
        try {
            partidoService.insertarPartido(partido, quinielaId);
            redirect.addFlashAttribute("mensaje", "Partido creado exitosamente.");
            if (quinielaId != null) {
                return "redirect:/admin/quinielas/" + quinielaId;
            }
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al crear partido: " + e.getMessage());
        }
        return "redirect:/admin/partidos";
    }

    /** Formulario para actualizar el resultado de un partido. */
    @GetMapping("/{id}/resultado")
    public String resultadoForm(@PathVariable Long id,
                                 @RequestParam(required = false) Long quinielaId,
                                 Model model) {
        Partido partido = partidoService.obtenerPorId(id);
        model.addAttribute("partido", partido);
        model.addAttribute("quinielaId", quinielaId);
        model.addAttribute("modoResultado", true);
        return "partidos/form";
    }

    /**
     * Actualiza el resultado del partido.
     * El SP ActualizarResultadoPartido llama internamente a CalcularPuntuacion.
     */
    @PostMapping("/{id}/resultado")
    public String actualizarResultado(@PathVariable Long id,
                                       @RequestParam Integer golesLocal,
                                       @RequestParam Integer golesVisitante,
                                       @RequestParam(required = false) Long quinielaId,
                                       RedirectAttributes redirect) {
        try {
            partidoService.actualizarResultado(id, golesLocal, golesVisitante);
            redirect.addFlashAttribute("mensaje", "Resultado actualizado. Puntuaciones calculadas.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        if (quinielaId != null) {
            return "redirect:/admin/quinielas/" + quinielaId;
        }
        return "redirect:/admin/partidos";
    }

    /** Elimina un partido. */
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id,
                            @RequestParam(required = false) Long quinielaId,
                            RedirectAttributes redirect) {
        try {
            partidoService.eliminarPartido(id);
            redirect.addFlashAttribute("mensaje", "Partido eliminado.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        if (quinielaId != null) {
            return "redirect:/admin/quinielas/" + quinielaId;
        }
        return "redirect:/admin/partidos";
    }
}
