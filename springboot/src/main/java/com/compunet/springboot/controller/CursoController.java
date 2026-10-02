package com.compunet.springboot.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.compunet.springboot.model.Curso;
import com.compunet.springboot.model.Profesor;
import com.compunet.springboot.service.CursoService;
import com.compunet.springboot.service.ProfesorService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cursos-mvc")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;
    private final ProfesorService profesorService;

    /**
     * 1. LISTAR: GET /cursos-mvc
     */
    @GetMapping
    public String listarCursos(Model model) {
        List<Curso> cursos = cursoService.listarTodos();
        model.addAttribute("titulo", "Gestión de Cursos Académicos");
        model.addAttribute("cursos", cursos);
        return "cursos/lista";
    }

    /**
     * 2. FORMULARIO NUEVO: GET /cursos-mvc/nuevo
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioCreacion(Model model) {
        Curso nuevo = new Curso();
        List<Profesor> profesores = profesorService.listarTodos();
        model.addAttribute("titulo", "Registrar Nuevo Curso");
        model.addAttribute("curso", nuevo);
        model.addAttribute("profesores", profesores);
        return "cursos/formulario";
    }

    /**
     * 3. GUARDAR (Crear / Editar): POST /cursos-mvc/guardar (PRG)
     */
    @PostMapping("/guardar")
    public String guardarCurso(@ModelAttribute("curso") Curso curso,
                               @RequestParam("profesorId") Long profesorId,
                               RedirectAttributes flash) {
        try {
            if (curso.getId() == null) {
                cursoService.registrarCurso(curso, profesorId);
                flash.addFlashAttribute("exito", "¡Curso registrado exitosamente!");
            } else {
                cursoService.actualizarCurso(curso.getId(), curso, profesorId);
                flash.addFlashAttribute("exito", "¡Curso actualizado exitosamente!");
            }
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/cursos-mvc/nuevo";
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error inesperado al procesar el curso.");
            return "redirect:/cursos-mvc";
        }
        return "redirect:/cursos-mvc";
    }

    /**
     * 4. FORMULARIO EDITAR: GET /cursos-mvc/editar/{id}
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
        Curso curso = cursoService.obtenerPorId(id).orElse(null);
        if (curso == null) {
            flash.addFlashAttribute("error", "El curso con ID " + id + " no existe.");
            return "redirect:/cursos-mvc";
        }
        List<Profesor> profesores = profesorService.listarTodos();
        model.addAttribute("titulo", "Editar Curso: " + curso.getNombre());
        model.addAttribute("curso", curso);
        model.addAttribute("profesores", profesores);
        return "cursos/formulario";
    }

    /**
     * 5. ELIMINAR: GET /cursos-mvc/eliminar/{id}
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarCurso(@PathVariable("id") Long id, RedirectAttributes flash) {
        try {
            cursoService.eliminarCurso(id);
            flash.addFlashAttribute("exito", "Curso eliminado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo eliminar el curso: " + e.getMessage());
        }
        return "redirect:/cursos-mvc";
    }
}
