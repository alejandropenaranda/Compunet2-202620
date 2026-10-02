package com.compunet.springboot.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.compunet.springboot.model.Permiso;
import com.compunet.springboot.service.PermisoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/permisos-mvc")
@RequiredArgsConstructor
public class PermisoController {

    private final PermisoService permisoService;

    /**
     * 1. LISTAR: GET /permisos-mvc
     */
    @GetMapping
    public String listarPermisos(Model model) {
        List<Permiso> permisos = permisoService.listarTodos();
        model.addAttribute("titulo", "Gestión de Permisos");
        model.addAttribute("permisos", permisos);
        return "permisos/lista";
    }

    /**
     * 2. FORMULARIO NUEVO: GET /permisos-mvc/nuevo
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioCreacion(Model model) {
        Permiso nuevo = new Permiso();
        model.addAttribute("titulo", "Registrar Nuevo Permiso");
        model.addAttribute("permiso", nuevo);
        return "permisos/formulario";
    }

    /**
     * 3. GUARDAR (Crear / Editar): POST /permisos-mvc/guardar (PRG)
     */
    @PostMapping("/guardar")
    public String guardarPermiso(@ModelAttribute("permiso") Permiso permiso, RedirectAttributes flash) {
        try {
            if (permiso.getId() == null) {
                permisoService.registrarPermiso(permiso);
                flash.addFlashAttribute("exito", "¡Permiso registrado exitosamente!");
            } else {
                permisoService.actualizarPermiso(permiso.getId(), permiso);
                flash.addFlashAttribute("exito", "¡Permiso actualizado exitosamente!");
            }
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/permisos-mvc/nuevo";
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error inesperado al procesar el permiso.");
            return "redirect:/permisos-mvc";
        }
        return "redirect:/permisos-mvc";
    }

    /**
     * 4. FORMULARIO EDITAR: GET /permisos-mvc/editar/{id}
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
        Permiso permiso = permisoService.obtenerPorId(id).orElse(null);
        if (permiso == null) {
            flash.addFlashAttribute("error", "El permiso con ID " + id + " no existe.");
            return "redirect:/permisos-mvc";
        }
        model.addAttribute("titulo", "Editar Permiso: " + permiso.getNombre());
        model.addAttribute("permiso", permiso);
        return "permisos/formulario";
    }

    /**
     * 5. ELIMINAR FÍSICAMENTE: GET /permisos-mvc/eliminar/{id}
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarPermiso(@PathVariable("id") Long id, RedirectAttributes flash) {
        try {
            permisoService.eliminarPermiso(id);
            flash.addFlashAttribute("exito", "Permiso eliminado correctamente del sistema.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo eliminar el permiso: " + e.getMessage());
        }
        return "redirect:/permisos-mvc";
    }
}
