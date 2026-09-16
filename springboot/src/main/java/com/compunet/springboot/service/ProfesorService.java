package com.compunet.springboot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compunet.springboot.model.Profesor;
import com.compunet.springboot.repository.ProfesorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfesorService {

    private final ProfesorRepository profesorRepository;

    @Transactional(readOnly = true)
    public List<Profesor> listarTodos() {
        return profesorRepository.findAll();
    }

    /**
     * Ejercicio 3: Obtener profesores activos por departamento sin distinguir mayúsculas/minúsculas.
     */
    @Transactional(readOnly = true)
    public List<Profesor> listarProfesoresActivosPorDepartamento(String depto) {
        return profesorRepository.findByDepartamentoIgnoreCaseAndActiveTrue(depto);
    }

    /**
     * Ejercicio 6: Obtener profesores por especialidad ordenados por apellido ascendente.
     */
    @Transactional(readOnly = true)
    public List<Profesor> listarPorEspecialidadOrdenados(String especialidad) {
        return profesorRepository.findByEspecialidadIgnoreCaseOrderByApellidoAsc(especialidad);
    }



    public List<Profesor> ejercicioDificil1(String depto, Integer creditos){
        return profesorRepository.findDistinctByDepartamentoIgnoreCaseAndCursos_CreditosGreaterThanEqualAndActiveTrueOrderByApellidoAscNombreAsc(depto, creditos);
    }
}
