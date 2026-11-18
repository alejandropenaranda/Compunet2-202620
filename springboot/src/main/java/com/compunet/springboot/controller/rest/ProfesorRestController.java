package com.compunet.springboot.controller.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.compunet.springboot.dto.Profesores.ProfesorResponseDTO;
import com.compunet.springboot.mapper.ProfesorMapper;
import com.compunet.springboot.service.ProfesorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/profesores")
@RequiredArgsConstructor
public class ProfesorRestController {

    private final ProfesorService profesorService;
    private final ProfesorMapper profesorMapper;

    /**
     * Listar todos los profesores registrados para asociarlos a cursos.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('COURSE_READ')")
    public ResponseEntity<List<ProfesorResponseDTO>> listarProfesores() {
        return ResponseEntity.ok(
                profesorMapper.toListProfesorResponseDTO(profesorService.listarTodos())
        );
    }

    /**
     * Consultar el detalle de un profesor por su identificador único.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_READ')")
    public ResponseEntity<ProfesorResponseDTO> obtenerProfesorPorId(@PathVariable Long id) {
        return profesorService.obtenerPorId(id)
                .map(profesorMapper::toProfesorResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
