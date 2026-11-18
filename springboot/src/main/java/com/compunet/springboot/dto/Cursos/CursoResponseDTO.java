package com.compunet.springboot.dto.Cursos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoResponseDTO {

    private Long id;
    private String nombre;
    private int creditos;
    private String departamento;
    private Long profesorId;
    private String profesorNombre;
    private String profesorApellido;
    private String profesorCorreo;
    private String profesorDepartamento;
}
