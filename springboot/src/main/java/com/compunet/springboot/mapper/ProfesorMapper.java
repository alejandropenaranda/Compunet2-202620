package com.compunet.springboot.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.compunet.springboot.dto.Profesores.ProfesorResponseDTO;
import com.compunet.springboot.model.Profesor;

@Mapper(componentModel = "spring")
public interface ProfesorMapper {

    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "nombre", source = "usuario.nombre")
    @Mapping(target = "apellido", source = "usuario.apellido")
    @Mapping(target = "correoInstitucional", source = "usuario.correoInstitucional")
    ProfesorResponseDTO toProfesorResponseDTO(Profesor profesor);

    List<ProfesorResponseDTO> toListProfesorResponseDTO(List<Profesor> profesores);
}
