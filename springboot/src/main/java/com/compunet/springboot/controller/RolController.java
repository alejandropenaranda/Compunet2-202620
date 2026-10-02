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

import com.compunet.springboot.model.Permiso;
import com.compunet.springboot.model.Rol;
import com.compunet.springboot.service.PermisoService;
import com.compunet.springboot.service.RolService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/roles-mvc")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;
    private final PermisoService permisoService;

    /**
     * 1. LISTAR: GET /roles-mvc
     */
    @GetMapping
    public String listarRoles(Model model) {
        List<Rol> roles = rolService.listarTodos();
        model.addAttribute("titulo", "Gestión de Roles y Seguridad");
        model.addAttribute("roles", roles);
        return "roles/lista";
    }

    /**
     * 2. FORMULARIO NUEVO: GET /roles-mvc/nuevo
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioCreacion(Model model) {
        Rol nuevo = new Rol();
        model.addAttribute("titulo", "Registrar Nuevo Rol");
        model.addAttribute("rol", nuevo);
        return "roles/formulario";
    }

    /**
     * 3. GUARDAR (Crear / Editar): POST /roles-mvc/guardar (PRG)
     */
    @PostMapping("/guardar")
    public String guardarRol(@ModelAttribute("rol") Rol rol, RedirectAttributes flash) {
        try {
            if (rol.getId() == null) {
                rolService.registrarRol(rol);
                flash.addFlashAttribute("exito", "¡Rol registrado exitosamente!");
            } else {
                rolService.actualizarRol(rol.getId(), rol);
                flash.addFlashAttribute("exito", "¡Rol actualizado exitosamente!");
            }
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/roles-mvc/nuevo";
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error inesperado al procesar el rol.");
            return "redirect:/roles-mvc";
        }
        return "redirect:/roles-mvc";
    }

    /**
     * 4. FORMULARIO EDITAR: GET /roles-mvc/editar/{id}
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
        Rol rol = rolService.obtenerPorId(id).orElse(null);
        if (rol == null) {
            flash.addFlashAttribute("error", "El rol con ID " + id + " no existe.");
            return "redirect:/roles-mvc";
        }
        List<Permiso> todosLosPermisos = permisoService.listarTodos();
        model.addAttribute("titulo", "Editar Rol: " + rol.getNombre());
        model.addAttribute("rol", rol);
        model.addAttribute("todosLosPermisos", todosLosPermisos);
        return "roles/formulario";
    }

    /**
     * 5. ASIGNAR PERMISO: POST /roles-mvc/{rolId}/permisos/asignar
     */
    @PostMapping("/{rolId}/permisos/asignar")
    public String asignarPermiso(@PathVariable("rolId") Long rolId,
                                 @RequestParam("permisoId") Long permisoId,
                                 RedirectAttributes flash) {
        try {
            rolService.agregarPermisoARol(rolId, permisoId);
            flash.addFlashAttribute("exito", "Permiso asignado correctamente al rol.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/roles-mvc/editar/" + rolId;
    }

    /**
     * 6. REMOVER PERMISO: GET /roles-mvc/{rolId}/permisos/remover/{permisoId}
     */
    @GetMapping("/{rolId}/permisos/remover/{permisoId}")
    public String removerPermiso(@PathVariable("rolId") Long rolId,
                                 @PathVariable("permisoId") Long permisoId,
                                 RedirectAttributes flash) {
        try {
            rolService.removerPermisoDeRol(rolId, permisoId);
            flash.addFlashAttribute("exito", "Permiso removido correctamente del rol.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/roles-mvc/editar/" + rolId;
    }

    /**
     * 7. ELIMINAR ROL: GET /roles-mvc/eliminar/{id}
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarRol(@PathVariable("id") Long id, RedirectAttributes flash) {
        try {
            rolService.eliminarRol(id);
            flash.addFlashAttribute("exito", "Rol eliminado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo eliminar el rol: " + e.getMessage());
        }
        return "redirect:/roles-mvc";
    }
}
