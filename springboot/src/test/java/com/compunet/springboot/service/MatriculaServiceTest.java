package com.compunet.springboot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.compunet.springboot.model.Curso;
import com.compunet.springboot.model.Matricula;
import com.compunet.springboot.model.MatriculaId;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.repository.CursoRepository;
import com.compunet.springboot.repository.MatriculaRepository;
import com.compunet.springboot.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - MatriculaService")
class MatriculaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @InjectMocks
    private MatriculaService matriculaService;

    private Usuario usuarioActivo;
    private Curso cursoNormal;

    @BeforeEach
    void setUp() {
        usuarioActivo = new Usuario();
        usuarioActivo.setId(1L);
        usuarioActivo.setNombre("Ana");
        usuarioActivo.setApellido("Gómez");
        usuarioActivo.setCorreoInstitucional("agomez@icesi.edu.co");
        usuarioActivo.setActive(true);

        cursoNormal = new Curso();
        cursoNormal.setId(101L);
        cursoNormal.setNombre("Computación en Internet II");
        cursoNormal.setCreditos(3);
    }

    @Test
    @DisplayName("Debe matricular correctamente a un estudiante activo en un curso")
    void debeMatricularEstudianteExitosamente() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioActivo));
        when(cursoRepository.findById(101L)).thenReturn(Optional.of(cursoNormal));
        when(matriculaRepository.existsById(any(MatriculaId.class))).thenReturn(false);
        when(matriculaRepository.save(any(Matricula.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        Matricula resultado = matriculaService.matricularEstudianteEnCurso(1L, 101L);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuarioActivo, resultado.getUsuario());
        assertEquals(cursoNormal, resultado.getCurso());
        verify(matriculaRepository, times(1)).save(any(Matricula.class));
    }

    @Test
    @DisplayName("Debe fallar si el estudiante está inactivo")
    void debeLanzarExcepcionSiEstudianteEstaInactivo() {
        // Arrange
        usuarioActivo.setActive(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioActivo));

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            matriculaService.matricularEstudianteEnCurso(1L, 101L);
        });

        assertTrue(ex.getMessage().contains("inactivo"));
        verifyNoInteractions(cursoRepository);
        verify(matriculaRepository, never()).save(any(Matricula.class));
    }

    @Test
    @DisplayName("Debe fallar si el estudiante ya está matriculado en el curso")
    void debeLanzarExcepcionSiYaEstaMatriculado() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioActivo));
        when(cursoRepository.findById(101L)).thenReturn(Optional.of(cursoNormal));
        when(matriculaRepository.existsById(new MatriculaId(1L, 101L))).thenReturn(true);

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            matriculaService.matricularEstudianteEnCurso(1L, 101L);
        });

        assertTrue(ex.getMessage().contains("ya se encuentra matriculado"));
        verify(matriculaRepository, never()).save(any(Matricula.class));
    }
}
