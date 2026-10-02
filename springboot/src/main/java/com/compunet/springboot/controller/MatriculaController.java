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

import com.compunet.springboot.model.Curso;
import com.compunet.springboot.model.Matricula;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.service.CursoService;
import com.compunet.springboot.service.MatriculaService;
import com.compunet.springboot.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/matriculas-mvc")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;
    private final UsuarioService usuarioService;
    private final CursoService cursoService;

    /**
     * 1. LISTAR: GET /matriculas-mvc
     */
    @GetMapping
    public String listarMatriculas(Model model) {
        List<Matricula> matriculas = matriculaService.listarTodas();
        model.addAttribute("titulo", "Gestión de Matrículas Académicas");
        model.addAttribute("matriculas", matriculas);
        return "matriculas/lista";
    }

    /**
     * 2. FORMULARIO NUEVA MATRÍCULA: GET /matriculas-mvc/nueva
     */
    @GetMapping("/nueva")
    public String mostrarFormularioMatricula(Model model) {
        // En el sistema, los estudiantes son usuarios activos (preferiblemente con rol ESTUDIANTE)
        List<Usuario> estudiantes = usuarioService.listarUsuariosActivosPorRol("ESTUDIANTE");
        if (estudiantes.isEmpty()) {
            estudiantes = usuarioService.listarUsuariosActivos();
        }
        List<Curso> cursos = cursoService.listarTodos();
        model.addAttribute("titulo", "Matricular Estudiante en Curso");
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("cursos", cursos);
        return "matriculas/formulario";
    }

    /**
     * 3. GUARDAR MATRÍCULA: POST /matriculas-mvc/guardar (PRG)
     */
    @PostMapping("/guardar")
    public String guardarMatricula(@RequestParam("usuarioId") Long usuarioId,
                                   @RequestParam("cursoId") Long cursoId,
                                   RedirectAttributes flash) {
        try {
            matriculaService.matricularEstudianteEnCurso(usuarioId, cursoId);
            flash.addFlashAttribute("exito", "¡Matrícula realizada exitosamente!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/matriculas-mvc/nueva";
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error inesperado al procesar la matrícula.");
            return "redirect:/matriculas-mvc";
        }
        return "redirect:/matriculas-mvc";
    }

    /**
     * 4. CANCELAR / ELIMINAR MATRÍCULA: GET /matriculas-mvc/eliminar/{usuarioId}/{cursoId}
     */
    @GetMapping("/eliminar/{usuarioId}/{cursoId}")
    public String eliminarMatricula(@PathVariable("usuarioId") Long usuarioId,
                                    @PathVariable("cursoId") Long cursoId,
                                    RedirectAttributes flash) {
        try {
            matriculaService.desmatricularEstudiante(usuarioId, cursoId);
            flash.addFlashAttribute("exito", "Matrícula cancelada/eliminada correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo cancelar la matrícula: " + e.getMessage());
        }
        return "redirect:/matriculas-mvc";
    }
}
