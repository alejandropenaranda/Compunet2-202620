package com.compunet.springboot.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.compunet.springboot.dto.Usuarios.UsuarioRequestDTO;
import com.compunet.springboot.dto.Usuarios.UsuarioResponseDTO;
import com.compunet.springboot.model.Rol;
import com.compunet.springboot.model.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "roles", target = "roles")
    UsuarioResponseDTO toUsuarioResponseDTO(Usuario usuario);

    List<UsuarioResponseDTO> toUsuarioResponseDTOList(List<Usuario> usuarios);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "roles", ignore = true)
    Usuario toEntity(UsuarioRequestDTO usuarioRequestDTO);

    default Set<String> rolesToStrings(List<Rol> roles) {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(Rol::getNombre)
                .collect(Collectors.toSet());
    }
}
