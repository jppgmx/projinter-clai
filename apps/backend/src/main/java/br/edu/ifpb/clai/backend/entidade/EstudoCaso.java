package br.edu.ifpb.clai.backend.entidade;

import jakarta.persistence.*;

@Entity
@Table(name = "estudo_de_caso")
public class EstudoCaso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estudo_caso")
    private Integer idEstudoCaso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_matricula_estudante", nullable = false)
    private Estudante estudante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_profissional_responsavel", nullable = false)
    private Profissional profissionalResponsavel;

    @Column(name = "periodo_letivo", nullable = false, length = 20)
    private String periodoLetivo;

    @Column(name = "datas_atendimento_anamnese", nullable = false, columnDefinition = "text")
    private String datasAtendimentoAnamnese;

    @Column(name = "origem_encaminhamento", nullable = false, length = 50)
    private String origemEncaminhamento;

    @Column(name = "origem_encaminhamento_outro", nullable = true, length = 150)
    private String origemEncaminhamentoOutro;

    @Column(name = "motivo_principal", nullable = false, columnDefinition = "text")
    private String motivoPrincipal;

    @Column(name = "demanda_estudante", nullable = true, columnDefinition = "text")
    private String demandaEstudante;

    @Column(name = "demanda_familia_responsavel", nullable = true, columnDefinition = "text")
    private String demandaFamiliaResponsavel;

    @Column(name = "demanda_equipe_escolar", nullable = true, columnDefinition = "text")
    private String demandaEquipeEscolar;

    @Column(name = "deficiencia_necessidade_informada", nullable = false, columnDefinition = "text")
    private String deficienciaNecessidadeInformada;

    @Column(name = "docs_comprobatorios_flag_quais", nullable = false, columnDefinition = "text")
    private String docsComprobatoriosFlagQuais;

    @Column(name = "condicao_associada_flag_quais", nullable = false, columnDefinition = "text")
    private String condicaoAssociadaFlagQuais;

    @Column(name = "medicacao_continua_flag_quais", nullable = false, columnDefinition = "text")
    private String medicacaoContinuaFlagQuais;

    protected EstudoCaso() {
    }

    public EstudoCaso(Estudante estudante, Profissional profissionalResponsavel,
                     String periodoLetivo,
                     String datasAtendimentoAnamnese,
                     String origemEncaminhamento,
                     String origemEncaminhamentoOutro,
                     String motivoPrincipal,
                     String demandaEstudante,
                     String demandaFamiliaResponsavel,
                     String demandaEquipeEscolar,
                     String deficienciaNecessidadeInformada,
                     String docsComprobatoriosFlagQuais,
                     String condicaoAssociadaFlagQuais,
                     String medicacaoContinuaFlagQuais) {
        this.estudante = estudante;
        this.profissionalResponsavel = profissionalResponsavel;
        this.periodoLetivo = periodoLetivo;
        this.datasAtendimentoAnamnese = datasAtendimentoAnamnese;
        this.origemEncaminhamento = origemEncaminhamento;
        this.origemEncaminhamentoOutro = origemEncaminhamentoOutro;
        this.motivoPrincipal = motivoPrincipal;
        this.demandaEstudante = demandaEstudante;
        this.demandaFamiliaResponsavel = demandaFamiliaResponsavel;
        this.demandaEquipeEscolar = demandaEquipeEscolar;
        this.deficienciaNecessidadeInformada = deficienciaNecessidadeInformada;
        this.docsComprobatoriosFlagQuais = docsComprobatoriosFlagQuais;
        this.condicaoAssociadaFlagQuais = condicaoAssociadaFlagQuais;
        this.medicacaoContinuaFlagQuais = medicacaoContinuaFlagQuais;
    }

    public Integer getIdEstudoCaso() { return idEstudoCaso; }
    public Estudante getEstudante() { return estudante; }
    public Profissional getProfissionalResponsavel() { return profissionalResponsavel; }
    public String getPeriodoLetivo() { return periodoLetivo; }
    public String getDatasAtendimentoAnamnese() { return datasAtendimentoAnamnese; }
    public String getOrigemEncaminhamento() { return origemEncaminhamento; }
    public String getOrigemEncaminhamentoOutro() { return origemEncaminhamentoOutro; }
    public String getMotivoPrincipal() { return motivoPrincipal; }
    public String getDemandaEstudante() { return demandaEstudante; }
    public String getDemandaFamiliaResponsavel() { return demandaFamiliaResponsavel; }
    public String getDemandaEquipeEscolar() { return demandaEquipeEscolar; }
    public String getDeficienciaNecessidadeInformada() { return deficienciaNecessidadeInformada; }
    public String getDocsComprobatoriosFlagQuais() { return docsComprobatoriosFlagQuais; }
    public String getCondicaoAssociadaFlagQuais() { return condicaoAssociadaFlagQuais; }
    public String getMedicacaoContinuaFlagQuais() { return medicacaoContinuaFlagQuais; }
}
