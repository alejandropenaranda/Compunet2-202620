package com.compunet.springboot.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.service.RolService;
import com.compunet.springboot.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    /**
     * 1. LISTAR: GET /usuarios
     */
    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> lista = usuarioService.listarUsuariosActivos();
        model.addAttribute("titulo", "Gestión de Usuarios Académicos");
        model.addAttribute("usuarios", lista);
        return "usuarios/lista";
    }

    /**
     * 2. MOSTRAR FORMULARIO CREACIÓN: GET /usuarios/nuevo
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioCreacion(Model model) {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setActive(true);
        rolService.obtenerPorNombre("ESTUDIANTE").ifPresent(r -> nuevoUsuario.getRoles().add(r));
        model.addAttribute("titulo", "Registrar Nuevo Usuario");
        model.addAttribute("usuario", nuevoUsuario);
        model.addAttribute("roles", rolService.listarTodos());
        return "usuarios/formulario";
    }

    /**
     * 3. GUARDAR (CREAR O EDITAR): POST /usuarios/guardar
     * Aplica el patrón POST-REDIRECT-GET (PRG)
     */
    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String guardarUsuario(@ModelAttribute("usuario") Usuario usuario,
            @RequestParam(value = "rolIds", required = false) List<Long> rolIds,
            RedirectAttributes flash) {
        try {
            if (usuario.getId() == null) {
                // Modo Creación: Delegado al Servicio
                usuarioService.registrarUsuario(usuario, rolIds);
                flash.addFlashAttribute("exito", "¡Usuario registrado exitosamente!");
            } else {
                // Modo Edición: Delegado al Servicio
                usuarioService.actualizarUsuario(usuario.getId(), usuario, rolIds);
                flash.addFlashAttribute("exito", "¡Usuario actualizado exitosamente!");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/nuevo";
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error inesperado al procesar la solicitud.");
            return "redirect:/usuarios";
        }

        return "redirect:/usuarios"; // Patrón PRG (HTTP 302)
    }

    /**
     * 4. MOSTRAR FORMULARIO EDICIÓN: GET /usuarios/editar/{id}
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
        Usuario usuario = usuarioService.obtenerPorId(id).orElse(null);
        if (usuario == null) {
            flash.addFlashAttribute("error", "El usuario con ID " + id + " no existe.");
            return "redirect:/usuarios";
        }

        model.addAttribute("titulo", "Editar Usuario: " + usuario.getNombre());
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.listarTodos());
        return "usuarios/formulario";
    }

    /**
     * 5. CAMBIAR ESTADO (Activar / Desactivar): GET /usuarios/desactivar/{id}
     */
    @GetMapping("/desactivar/{id}")
    public String alternarEstadoUsuario(@PathVariable("id") Long id, RedirectAttributes flash) {
        try {
            Usuario u = usuarioService.alternarEstado(id);
            String estadoStr = u.isActive() ? "activado" : "desactivado";
            flash.addFlashAttribute("exito", "Usuario " + u.getNombre() + " " + estadoStr + " correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    /**
     * 6. ELIMINAR FÍSICAMENTE: GET /usuarios/eliminar/{id}
     */
    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasAuthoritiy(USER_DELETE)")
    public String eliminarUsuario(@PathVariable("id") Long id, RedirectAttributes flash) {
        try {
            usuarioService.eliminarUsuario(id);
            flash.addFlashAttribute("exito", "Usuario eliminado correctamente del sistema.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
