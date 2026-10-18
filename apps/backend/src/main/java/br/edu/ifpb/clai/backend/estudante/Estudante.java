package br.edu.ifpb.clai.backend.estudante;

import jakarta.persistence.*;

@Entity
@Table(name = "estudante")
public class Estudante {
    @Id
    @Column(name = "matricula", length = 30, nullable = false)
    private String matricula;

    @Column(name = "turma", length = 50, nullable = false)
    private String turma;

    @Column(name = "nome_estudante", length = 150, nullable = false)
    private String nomeEstudante;

    @Column(name = "periodo_ingresso", length = 20, nullable = false)
    private String periodoIngresso;

    @Column(name = "data_nascimento_idade", length = 60, nullable = false)
    private String dataNascimentoIdade;

    @Column(name = "telefone_estudante", length = 30, nullable = true)
    private String telefoneEstudante;

    @Column(name = "email_estudante", length = 150, nullable = true)
    private String emailEstudante;

    @Column(name = "nome_responsavel", length = 150, nullable = true)
    private String nomeResponsavel;

    @Column(name = "grau_parentesco", length = 60, nullable = true)
    private String grauParentesco;

    @Column(name = "telefone_responsavel", length = 30, nullable = true)
    private String telefoneResponsavel;

    @Column(name = "email_responsavel", length = 150, nullable = true)
    private String emailResponsavel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_curso", nullable = false)
    private Curso curso;

    protected Estudante() {
    }

    public Estudante(RegistroEstudante dados, Curso curso) {
        this.matricula = dados.matricula();
        this.turma = dados.turma();
        this.nomeEstudante = dados.nomeEstudante();
        this.periodoIngresso = dados.periodoIngresso();
        this.dataNascimentoIdade = dados.dataNascimentoIdade();
        this.telefoneEstudante = dados.telefoneEstudante();
        this.emailEstudante = dados.emailEstudante();
        this.nomeResponsavel = dados.nomeResponsavel();
        this.grauParentesco = dados.grauParentesco();
        this.telefoneResponsavel = dados.telefoneResponsavel();
        this.emailResponsavel = dados.emailResponsavel();
        this.curso = curso;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getTurma() {
        return turma;
    }

    public String getNomeEstudante() {
        return nomeEstudante;
    }

    public String getPeriodoIngresso() {
        return periodoIngresso;
    }

    public String getDataNascimentoIdade() {
        return dataNascimentoIdade;
    }

    public String getTelefoneEstudante() {
        return telefoneEstudante;
    }

    public String getEmailEstudante() {
        return emailEstudante;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public String getGrauParentesco() {
        return grauParentesco;
    }

    public String getTelefoneResponsavel() {
        return telefoneResponsavel;
    }

    public String getEmailResponsavel() {
        return emailResponsavel;
    }

    public Curso getCurso() {
        return curso;
    }
}
