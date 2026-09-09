package com.compunet.springboot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@EqualsAndHashCode // Fundamental para que JPA pueda comparar las llaves
public class EstudianteCursoId {

    @Column (name = "estudiante_id")
    private Long estudianteId;

    @Column (name = "curso_id")
    private Long cursoId;
}
