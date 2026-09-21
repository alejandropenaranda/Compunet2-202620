package com.compunet.springboot.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.compunet.springboot.model.Rol;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.repository.RolRepository;
import com.compunet.springboot.repository.UsuarioRepository;
import com.compunet.springboot.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - UsuarioService - Mockito")
public class UsuarioServiceTest {

    @Mock 
    private UsuarioRepository usuarioRepository;

    @Mock 
    private RolRepository rolRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioEjemplo;
    private Rol rolEstudiante;
    
    @BeforeEach
    void setUp() {
        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setId(1L);
        usuarioEjemplo.setNombre("Carlo");
        usuarioEjemplo.setApellido("Perez");
        usuarioEjemplo.setCorreoInstitucional("cPerez@icesi.edu.co");
        usuarioEjemplo.setPassword("Secreto123");

        rolEstudiante = new Rol();
        rolEstudiante.setId(10L);
        rolEstudiante.setNombre("ESTUDIANTE");
    }

    @Test 
    @DisplayName ("Debe registrar un usuario exitosamente cuando los datos y el rol son válidos")
    void debeRegistrarUsuarioExitosamente(){

        //1. Arrange
        when(usuarioRepository.existsByCorreoInstitucional(usuarioEjemplo.getCorreoInstitucional())).thenReturn(false);
        when(rolRepository.findByNombre("ESTUDIANTE")).thenReturn(Optional.of(rolEstudiante));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //2. Act
        Usuario resultado = usuarioService.registrarUsuario(usuarioEjemplo, "ESTUDIANTE");

        //3. Assert
        assertNotNull(resultado);
        assertTrue(resultado.isActive());
        assertTrue(resultado.getRoles().contains(rolEstudiante));
    }
    
    @Test 
    @DisplayName ("Debe lanzar excepción cuando el correo institucional ya está registrado")
    void debeLanzarExcepcionCuandoCorreoYaExiste() {

        //1. Arrange
        when(usuarioRepository.existsByCorreoInstitucional(usuarioEjemplo.getCorreoInstitucional())).thenReturn(true);

        //2. Act y 3. Assert

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(usuarioEjemplo, "ESTUDIANTE");
        });

        assertTrue(exception.getMessage().contains("ya se encuentra registrado"));

        verify(rolRepository, never()).findByNombre(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test 
    @DisplayName ("Debe lanzar excepción cuando el rol especificado no existe")
    void debeLanzarExcepcionCuandoRolNoExiste() {
        //1. Arrange
        when(usuarioRepository.existsByCorreoInstitucional(usuarioEjemplo.getCorreoInstitucional())).thenReturn(false);
        when(rolRepository.findByNombre("ROL_FANTASMA")).thenReturn(Optional.empty());

        //2. Act and 3. Assert

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            usuarioService.registrarUsuario(usuarioEjemplo, "ROL_FANTASMA");
        });

        assertTrue(exception.getMessage().contains("El rol especificado no existe"));

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
