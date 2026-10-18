package com.compunet.springboot.dto.Usuarios;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class UsuarioRequestDTO {
    
    private String nombre;
    private String apellido;
    private String correoInstitucional;
    private String password;
    private Set<String> roles;
    
}
