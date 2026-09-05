package com.compunet.springboot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table (name = "Curso")
public class Curso {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "nombre", nullable = false)
    private String nombre;

    @Column (name = "creditos", nullable = false)
    private int creditos;

    @Column (name = "departamento", nullable = false)
    private String departamento;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "profesor_id", nullable = false)
    private Profesor profesor;

}
