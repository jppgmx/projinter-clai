package br.edu.ifpb.clai.backend.entidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "curso")
public class Curso {
    @Id
    @Column(name = "id_curso")
    private Integer idCurso;

    @Column(name = "nome_curso", nullable = false, length = 150)
    private String nomeCurso;

    protected Curso() {
    }

    public Integer getIdCurso() { return idCurso; }
    public String getNomeCurso() { return nomeCurso; }
}
