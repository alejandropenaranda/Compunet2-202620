package com.compunet.springboot.dto.Profesores;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfesorResponseDTO {

    private Long id;
    private Long usuarioId;
    private String nombre;
    private String apellido;
    private String correoInstitucional;
    private String especialidad;
    private String departamento;
}
