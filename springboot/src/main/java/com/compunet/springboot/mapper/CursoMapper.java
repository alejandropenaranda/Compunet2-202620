package com.compunet.springboot.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.compunet.springboot.dto.Cursos.CursoRequestDTO;
import com.compunet.springboot.dto.Cursos.CursoResponseDTO;
import com.compunet.springboot.model.Curso;

@Mapper(componentModel = "spring")
public interface CursoMapper {

    @Mapping(target = "profesorId", source = "profesor.id")
    @Mapping(target = "profesorNombre", source = "profesor.usuario.nombre")
    @Mapping(target = "profesorApellido", source = "profesor.usuario.apellido")
    @Mapping(target = "profesorCorreo", source = "profesor.usuario.correoInstitucional")
    @Mapping(target = "profesorDepartamento", source = "profesor.departamento")
    CursoResponseDTO toCursoResponseDTO(Curso curso);

    List<CursoResponseDTO> toListCursoResponseDTO(List<Curso> cursos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profesor", ignore = true)
    @Mapping(target = "matriculas", ignore = true)
    Curso toCurso(CursoRequestDTO requestDTO);
}
