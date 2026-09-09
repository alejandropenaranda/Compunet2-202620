package com.compunet.springboot.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "Profesor")
public class Profesor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column (name = "apellido", nullable = false)
    private String apellido;
    
    @Column (name = "correo_institucional", nullable = false, unique = true, length = 50)
    private String correoInstitucional;
    
    @Column (name = "especialidad", nullable = false)
    private String especialidad;

    @Column (name = "departamento", nullable = false)
    private String departamento;

    @Column (name = "active", nullable = false)
    private boolean active;

    @JsonIgnoreProperties (value = "profesor")
    @OneToMany(mappedBy = "profesor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Curso> cursos = new ArrayList<>();
    
}
