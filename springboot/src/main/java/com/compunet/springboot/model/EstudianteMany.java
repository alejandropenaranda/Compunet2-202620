package com.compunet.springboot.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity 
@Getter
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
@Table(name = "estudiante_many")
public class EstudianteMany {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "correo_institucional", nullable = false, unique = true, length = 50)
    private String correoInstitucional;

    @Column(name = "active", nullable = false)
    private boolean active;

    // Ejemplo rápido de cómo sería un @ManyToMany puro dentro de Estudiante.java
    // SIN crear la entidad Matricula:

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // Evitar cascade ALL y REMOVE porque si se elimina un estudiante, 
    @JoinTable(                                                     // intentara eliminar los cursos asociados
    name = "Estudiante_Curso_Many", 
    joinColumns = @JoinColumn(name = "estudiante_id"), 
    inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    private List<CursoMany> cursos = new ArrayList<>();
}
