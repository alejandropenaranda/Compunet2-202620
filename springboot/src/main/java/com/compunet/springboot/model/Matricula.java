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
@Table(name = "matricula")
public class Matricula {

    @EmbeddedId
    private MatriculaId id = new MatriculaId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JsonIgnoreProperties("matriculas")
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cursoId")
    @JsonIgnoreProperties("matriculas")
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    public Matricula(Usuario usuario, Curso curso) {
        this.usuario = usuario;
        this.curso = curso;
        this.id = new MatriculaId(usuario.getId(), curso.getId());
    }
}
