package com.compunet.springboot.dto.Cursos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoRequestDTO {

    private String nombre;
    private Integer creditos;
    private String departamento;
    private Long profesorId;
}
