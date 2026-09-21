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
import com.compunet.springboot.model.Estudiante;
import com.compunet.springboot.model.EstudianteCurso;
import com.compunet.springboot.model.EstudianteCursoId;
import com.compunet.springboot.repository.CursoRepository;
import com.compunet.springboot.repository.EstudianteCursoRepository;
import com.compunet.springboot.repository.EstudianteRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - MatriculaService")
class MatriculaServiceTest {

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private EstudianteCursoRepository estudianteCursoRepository;

    @InjectMocks
    private MatriculaService matriculaService;

    private Estudiante estudianteActivo;
    private Curso cursoNormal;

    @BeforeEach
    void setUp() {
        estudianteActivo = new Estudiante();
        estudianteActivo.setId(1L);
        estudianteActivo.setNombre("Ana");
        estudianteActivo.setActive(true);

        cursoNormal = new Curso();
        cursoNormal.setId(101L);
        cursoNormal.setNombre("Computación en Internet II");
        cursoNormal.setCreditos(3);
    }

    @Test
    @DisplayName("Debe matricular correctamente a un estudiante activo en un curso con cupo de créditos")
    void debeMatricularEstudianteExitosamente() throws Exception {
        // Arrange
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteActivo));
        when(cursoRepository.findById(101L)).thenReturn(Optional.of(cursoNormal));
        when(estudianteCursoRepository.existsById(any(EstudianteCursoId.class))).thenReturn(false);
        when(estudianteCursoRepository.save(any(EstudianteCurso.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        EstudianteCurso resultado = matriculaService.matricularEstudianteEnCurso(1L, 101L);

        // Assert
        assertNotNull(resultado);
        assertEquals(estudianteActivo, resultado.getEstudiante());
        assertEquals(cursoNormal, resultado.getCurso());
        verify(estudianteCursoRepository, times(1)).save(any(EstudianteCurso.class));
    }

    @Test
    @DisplayName("Debe fallar si el estudiante está inactivo")
    void debeLanzarExcepcionSiEstudianteEstaInactivo() {
        // Arrange
        estudianteActivo.setActive(false);
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteActivo));

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            matriculaService.matricularEstudianteEnCurso(1L, 101L);
        });

        assertTrue(ex.getMessage().contains("inactivo"));
        verifyNoInteractions(cursoRepository);
        verify(estudianteCursoRepository, never()).save(any(EstudianteCurso.class));
    }

    @Test
    @DisplayName("Debe fallar si el estudiante ya está matriculado en el curso")
    void debeLanzarExcepcionSiYaEstaMatriculado() {
        // Arrange
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteActivo));
        when(cursoRepository.findById(101L)).thenReturn(Optional.of(cursoNormal));
        when(estudianteCursoRepository.existsById(new EstudianteCursoId(1L, 101L))).thenReturn(true);

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            matriculaService.matricularEstudianteEnCurso(1L, 101L);
        });

        assertTrue(ex.getMessage().contains("ya se encuentra matriculado"));
        verify(estudianteCursoRepository, never()).save(any(EstudianteCurso.class));
    }

}
