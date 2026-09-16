package com.compunet.springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "estudiante_curso")
public class EstudianteCurso {

    @EmbeddedId
    private EstudianteCursoId id = new EstudianteCursoId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("estudianteId")
    @JsonIgnoreProperties("estudianteCursos")
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cursoId")
    @JsonIgnoreProperties("estudianteCursos")
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    public EstudianteCurso(Estudiante estudiante, Curso curso){
        this.estudiante = estudiante;
        this.curso = curso;
        this.id = new EstudianteCursoId(estudiante.getId(),curso.getId());
    }
}