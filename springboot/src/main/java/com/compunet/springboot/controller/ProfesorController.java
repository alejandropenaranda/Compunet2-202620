package com.compunet.springboot.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.compunet.springboot.model.Profesor;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.service.ProfesorService;
import com.compunet.springboot.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/profesores-mvc")
@RequiredArgsConstructor
public class ProfesorController {

    private final ProfesorService profesorService;
    private final UsuarioService usuarioService;

    /**
     * 1. LISTAR: GET /profesores-mvc
     */
    @GetMapping
    public String listarProfesores(Model model) {
        List<Profesor> profesores = profesorService.listarTodos();
        model.addAttribute("titulo", "Gestión de Profesores (Cuerpo Docente)");
        model.addAttribute("profesores", profesores);
        return "profesores/lista";
    }

    /**
     * 2. FORMULARIO NUEVO PERFIL DOCENTE: GET /profesores-mvc/nuevo
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioCreacion(Model model) {
        Profesor nuevo = new Profesor();
        List<Usuario> usuarios = usuarioService.listarUsuariosActivos();
        model.addAttribute("titulo", "Registrar Perfil Docente");
        model.addAttribute("profesor", nuevo);
        model.addAttribute("usuarios", usuarios);
        return "profesores/formulario";
    }

    /**
     * 3. GUARDAR (Crear / Editar): POST /profesores-mvc/guardar (PRG)
     */
    @PostMapping("/guardar")
    public String guardarProfesor(@RequestParam(value = "usuarioId", required = false) Long usuarioId,
                                  @RequestParam(value = "profesorId", required = false) Long profesorId,
                                  @RequestParam("especialidad") String especialidad,
                                  @RequestParam("departamento") String departamento,
                                  RedirectAttributes flash) {
        try {
            if (profesorId == null || profesorId == 0) {
                profesorService.registrarProfesor(usuarioId, especialidad, departamento);
                flash.addFlashAttribute("exito", "¡Perfil docente asignado exitosamente!");
            } else {
                profesorService.actualizarProfesor(profesorId, especialidad, departamento);
                flash.addFlashAttribute("exito", "¡Perfil docente actualizado exitosamente!");
            }
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/profesores-mvc/nuevo";
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error inesperado al procesar el profesor.");
            return "redirect:/profesores-mvc";
        }
        return "redirect:/profesores-mvc";
    }

    /**
     * 4. FORMULARIO EDITAR: GET /profesores-mvc/editar/{id}
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
        Profesor profesor = profesorService.obtenerPorId(id).orElse(null);
        if (profesor == null) {
            flash.addFlashAttribute("error", "El profesor con ID " + id + " no existe.");
            return "redirect:/profesores-mvc";
        }
        model.addAttribute("titulo", "Editar Perfil Docente: " + profesor.getUsuario().getNombre());
        model.addAttribute("profesor", profesor);
        return "profesores/formulario";
    }

    /**
     * 5. ELIMINAR PERFIL DOCENTE: GET /profesores-mvc/eliminar/{id}
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarProfesor(@PathVariable("id") Long id, RedirectAttributes flash) {
        try {
            profesorService.eliminarProfesor(id);
            flash.addFlashAttribute("exito", "Perfil docente removido correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo eliminar el perfil docente: " + e.getMessage());
        }
        return "redirect:/profesores-mvc";
    }
}
