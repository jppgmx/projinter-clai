package br.edu.ifpb.clai.backend.entidade;

import jakarta.persistence.*;

@Entity
@Table(name = "profissional")
public class Profissional {
    @Id
    @Column(name = "id_profissional")
    private Integer idProfissional;
    @Column(name = "nome_profissional", nullable = false, length = 150)
    private String nomeProfissional;
    @Column(name = "papel_cargo", nullable = false, length = 100)
    private String papelCargo;

    protected Profissional() {
    }

    public Integer getIdProfissional() { return idProfissional; }
    public String getNomeProfissional() { return nomeProfissional; }
    public String getPapelCargo() { return papelCargo; }
}
