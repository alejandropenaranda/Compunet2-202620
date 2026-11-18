package com.compunet.springboot.controller.rest;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.compunet.springboot.dto.Cursos.CursoRequestDTO;
import com.compunet.springboot.dto.Cursos.CursoResponseDTO;
import com.compunet.springboot.mapper.CursoMapper;
import com.compunet.springboot.model.Curso;
import com.compunet.springboot.service.CursoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cursos")
@RequiredArgsConstructor
public class CursoRestController {

    private final CursoService cursoService;
    private final CursoMapper cursoMapper;

    /**
     * Listar todos los cursos disponibles o filtrarlos por departamento (case-insensitive).
     */
    @GetMapping
    @PreAuthorize("hasAuthority('COURSE_READ')")
    public ResponseEntity<List<CursoResponseDTO>> listarCursos(
            @RequestParam(name = "departamento", required = false) String departamento) {
        List<Curso> cursos;
        if (departamento != null && !departamento.isBlank()) {
            cursos = cursoService.listarPorDepartamento(departamento);
        } else {
            cursos = cursoService.listarTodos();
        }
        return ResponseEntity.ok(cursoMapper.toListCursoResponseDTO(cursos));
    }

    /**
     * Consultar detalle de un curso por su identificador único.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_READ')")
    public ResponseEntity<CursoResponseDTO> obtenerCursoPorId(@PathVariable Long id) {
        return cursoService.obtenerPorId(id)
                .map(cursoMapper::toCursoResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Consultar cursos a cargo de un docente específico mediante su profesorId.
     */
    @GetMapping("/profesor/{profesorId}")
    @PreAuthorize("hasAuthority('COURSE_READ')")
    public ResponseEntity<List<CursoResponseDTO>> listarCursosPorProfesor(@PathVariable Long profesorId) {
        try {
            List<Curso> cursos = cursoService.listarPorProfesorId(profesorId);
            return ResponseEntity.ok(cursoMapper.toListCursoResponseDTO(cursos));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Registrar un nuevo curso asignando su profesor responsable.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('COURSE_WRITE')")
    public ResponseEntity<CursoResponseDTO> crearCurso(@RequestBody CursoRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getNombre() == null || requestDTO.getNombre().isBlank()
                || requestDTO.getCreditos() == null || requestDTO.getCreditos() <= 0
                || requestDTO.getDepartamento() == null || requestDTO.getDepartamento().isBlank()
                || requestDTO.getProfesorId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            Curso curso = cursoMapper.toCurso(requestDTO);
            Curso creado = cursoService.registrarCurso(curso, requestDTO.getProfesorId());

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(creado.getId())
                    .toUri();

            return ResponseEntity.created(location).body(cursoMapper.toCursoResponseDTO(creado));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Actualizar los datos de un curso existente y/o su profesor asignado.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_WRITE')")
    public ResponseEntity<CursoResponseDTO> actualizarCurso(
            @PathVariable Long id,
            @RequestBody CursoRequestDTO requestDTO) {
        if (requestDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            Curso datosNuevos = cursoMapper.toCurso(requestDTO);
            Curso actualizado = cursoService.actualizarCurso(id, datosNuevos, requestDTO.getProfesorId());
            return ResponseEntity.ok(cursoMapper.toCursoResponseDTO(actualizado));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Eliminar un curso existente.
     * Restringido exclusivamente al rol ADMINISTRADOR.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarCurso(@PathVariable Long id) {
        try {
            cursoService.eliminarCurso(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
