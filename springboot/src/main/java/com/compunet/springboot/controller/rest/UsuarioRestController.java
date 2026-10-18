package com.compunet.springboot.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.compunet.springboot.dto.Usuarios.UsuarioRequestDTO;
import com.compunet.springboot.dto.Usuarios.UsuarioResponseDTO;
import com.compunet.springboot.mapper.UsuarioMapper;
import com.compunet.springboot.model.Rol;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.service.UsuarioService;

import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor 
public class UsuarioRestController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    @GetMapping
    public List<UsuarioResponseDTO> getUsuarios(@RequestParam(name ="activo", required = false) Boolean active) {

        List<Usuario> usuarios = (active != null) 
            ? usuarioService.listarUsuarios(active)
            : usuarioService.listarUsuarios();

        // return usuarios.stream()
        //         .map(usuario -> UsuarioResponseDTO.builder()
        //                 .id(usuario.getId())
        //                 .nombre(usuario.getNombre())
        //                 .apellido(usuario.getApellido())
        //                 .correoInstitucional(usuario.getCorreoInstitucional())
        //                 .active(usuario.isActive())
        //                 .roles(usuario.getRoles().stream()
        //                         .map(Rol::getNombre)
        //                         .collect(Collectors.toSet()))
        //                 .build())
        //         .toList();

        return usuarioMapper.toUsuarioResponseDTOList(usuarios);
    }

    /**
     * 2. GET /api/v1/usuarios/{id}
     * Consulta por ID con directivas de caché y control 404 NOT_FOUND.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(id);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UsuarioResponseDTO response = usuarioMapper.toUsuarioResponseDTO(usuarioOpt.get());
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS).cachePrivate())
                .eTag(String.valueOf(response.hashCode()))
                .body(response);
    }

    /**
     * 3. POST /api/v1/usuarios
     * Crea un nuevo usuario procesando sus roles reales en la BD y retorna 201 Created con Location.
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(@RequestBody UsuarioRequestDTO requestDTO) {
        try {
            Usuario usuario = usuarioMapper.toEntity(requestDTO);
            // Pasamos los datos del usuario y el Set<String> roles del DTO para su resolución en BD
            Usuario creado = usuarioService.registrarUsuario(usuario, requestDTO.getRoles());

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(creado.getId())
                    .toUri();

            return ResponseEntity.created(location).body(usuarioMapper.toUsuarioResponseDTO(creado));
        } catch (IllegalArgumentException e) {
            // El correo institucional ya se encuentra registrado
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * 4. PUT /api/v1/usuarios/{id}
     * Reemplazo total e idempotente del recurso.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioRequestDTO requestDTO) {
        try {
            Usuario datosNuevos = usuarioMapper.toEntity(requestDTO);
            Usuario actualizado = usuarioService.actualizarUsuario(id, datosNuevos, requestDTO.getRoles());
            return ResponseEntity.ok(usuarioMapper.toUsuarioResponseDTO(actualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 5. PATCH /api/v1/usuarios/{id}/estado
     * Modificación parcial usando el método alternarEstado de UsuarioService.
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponseDTO> alternarEstado(@PathVariable Long id) {
        try {
            Usuario usuarioModificado = usuarioService.alternarEstado(id);
            return ResponseEntity.ok(usuarioMapper.toUsuarioResponseDTO(usuarioModificado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 6. DELETE /api/v1/usuarios/{id}
     * Eliminación con respuesta semántica 204 No Content (sin cuerpo).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
