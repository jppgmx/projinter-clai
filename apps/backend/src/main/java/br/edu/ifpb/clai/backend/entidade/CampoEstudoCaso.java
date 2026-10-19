package br.edu.ifpb.clai.backend.entidade;

import java.util.Arrays;
import java.util.Optional;

/** Lista fechada dos campos textuais que a T11 pode editar. */
public enum CampoEstudoCaso {
    PERIODO_LETIVO("periodoLetivo", "periodo_letivo", 20),
    DATAS_ATENDIMENTO_ANAMNESE("datasAtendimentoAnamnese", "datas_atendimento_anamnese", 0),
    ORIGEM_ENCAMINHAMENTO("origemEncaminhamento", "origem_encaminhamento", 50),
    ORIGEM_ENCAMINHAMENTO_OUTRO("origemEncaminhamentoOutro", "origem_encaminhamento_outro", 150),
    MOTIVO_PRINCIPAL("motivoPrincipal", "motivo_principal", 0),
    DEMANDA_ESTUDANTE("demandaEstudante", "demanda_estudante", 0),
    DEMANDA_FAMILIA_RESPONSAVEL("demandaFamiliaResponsavel", "demanda_familia_responsavel", 0),
    DEMANDA_EQUIPE_ESCOLAR("demandaEquipeEscolar", "demanda_equipe_escolar", 0),
    DEFICIENCIA_NECESSIDADE_INFORMADA("deficienciaNecessidadeInformada", "deficiencia_necessidade_informada", 0),
    DIAGNOSTICOS("diagnosticos", "diagnosticos", 0),
    DOCS_COMPROBATORIOS_FLAG_QUAIS("docsComprobatoriosFlagQuais", "docs_comprobatorios_flag_quais", 0),
    DATA_PROFISSIONAL_DOCS("dataProfissionalDocs", "data_profissional_docs", 0),
    CONDICAO_ASSOCIADA_FLAG_QUAIS("condicaoAssociadaFlagQuais", "condicao_associada_flag_quais", 0),
    MEDICACAO_CONTINUA_FLAG_QUAIS("medicacaoContinuaFlagQuais", "medicacao_continua_flag_quais", 0),
    ENCAMINHAMENTOS_ANTERIORES("encaminhamentosAnteriores", "encaminhamentos_anteriores", 0),
    SERVICOS_APOIO_ATUAIS_ANTERIORES("servicosApoioAtuaisAnteriores", "servicos_apoio_atuais_anteriores", 0),
    HISTORICO_ESCOLAR("historicoEscolar", "historico_escolar", 0),
    EXPERIENCIAS_ESCOLARES_POSITIVAS("experienciasEscolaresPositivas", "experiencias_escolares_positivas", 0),
    EXPERIENCIAS_NEGATIVAS_BARREIRAS("experienciasNegativasBarreiras", "experiencias_negativas_barreiras", 0),
    ADAPTACOES_RECURSOS_UTILIZADOS_RESULTADOS("adaptacoesRecursosUtilizadosResultados", "adaptacoes_recursos_utilizados_resultados", 0),
    OBJETIVOS_CONHECIMENTOS_CONSOLIDADOS("objetivosConhecimentosConsolidados", "objetivos_conhecimentos_consolidados", 0),
    AREAS_DISCIPLINAS_MAIOR_FACILIDADE("areasDisciplinasMaiorFacilidade", "areas_disciplinas_maior_facilidade", 0),
    AREAS_DISCIPLINAS_MAIOR_DIFICULDADE("areasDisciplinasMaiorDificuldade", "areas_disciplinas_maior_dificuldade", 0),
    COMUNICACAO_COMPREENSAO_ORIENTACOES("comunicacaoCompreensaoOrientacoes", "comunicacao_compreensao_orientacoes", 0),
    LEITURA_ESCRITA_LINGUAGEM("leituraEscritaLinguagem", "leitura_escrita_linguagem", 0),
    RACIOCINIO_LOGICO_MATEMATICO("raciocinioLogicoMatematico", "raciocinio_logico_matematico", 0),
    ATENCAO_MEMORIA_FUNCOES_EXECUTIVAS("atencaoMemoriaFuncoesExecutivas", "atencao_memoria_funcoes_executivas", 0),
    ORGANIZACAO_PLANEJAMENTO_TAREFAS("organizacaoPlanejamentoTarefas", "organizacao_planejamento_tarefas", 0),
    INTERACAO_SOCIAL_PARTICIPACAO_SALA("interacaoSocialParticipacaoSala", "interacao_social_participacao_sala", 0),
    ASPECTOS_SENSORIAIS_RELEVANTES("aspectosSensoriaisRelevantes", "aspectos_sensoriais_relevantes", 0),
    MOBILIDADE_ACESSO_FISICO_TECNOLOGIAS("mobilidadeAcessoFisicoTecnologias", "mobilidade_acesso_fisico_tecnologias", 0),
    AUTONOMIA_CONTEXTO_FAMILIAR("autonomiaContextoFamiliar", "autonomia_contexto_familiar", 0),
    AUTONOMIA_CONTEXTO_ESCOLAR("autonomiaContextoEscolar", "autonomia_contexto_escolar", 0),
    ESTRATEGIAS_FAVORECEM_APRENDIZAGEM("estrategiasFavorecemAprendizagem", "estrategias_favorecem_aprendizagem", 0),
    SITUACOES_DIFICULTAM_DESORGANIZAM_APRENDIZAGEM("situacoesDificultamDesorganizamAprendizagem", "situacoes_dificultam_desorganizam_aprendizagem", 0),
    RELACAO_CURSO_EXPECTATIVAS("relacaoCursoExpectativas", "relacao_curso_expectativas", 0),
    REDE_APOIO_FAMILIAR_SOCIAL("redeApoioFamiliarSocial", "rede_apoio_familiar_social", 0),
    CONFLITOS_FATORES_PSICOSSOCIAIS("conflitosFatoresPsicossociais", "conflitos_fatores_psicossociais", 0),
    OBSERVACOES_FREQUENCIA_ROTINA_PERMANENCIA("observacoesFrequenciaRotinaPermanencia", "observacoes_frequencia_rotina_permanencia", 0),
    POTENCIALIDADES_ACADEMICAS("potencialidadesAcademicas", "potencialidades_academicas", 0),
    INTERESSES_HABILIDADES("interessesHabilidades", "interesses_habilidades", 0),
    FORMAS_PREFERENCIAIS_APRENDER("formasPreferenciaisAprender", "formas_preferenciais_aprender", 0),
    RECURSOS_PESSOAIS_TECNOLOGICOS("recursosPessoaisTecnologicos", "recursos_pessoais_tecnologicos", 0),
    SINTESE_A_IDENTIFICACAO_NECESSIDADES("sinteseAIdentificacaoNecessidades", "sintese_a_identificacao_necessidades", 0),
    SINTESE_B_AVANCOS_OBJETIVOS_ALCANCADOS("sinteseBAvancosObjetivosAlcancados", "sintese_b_avancos_objetivos_alcancados", 0),
    SINTESE_C_DIFICULDADES_BARREIRAS_EDUCACIONAIS("sinteseCDificuldadesBarreirasEducacionais", "sintese_c_dificuldades_barreiras_educacionais", 0),
    SINTESE_D_POTENCIALIDADES_CONDICOES_FACILITADORAS("sinteseDPotencialidadesCondicoesFacilitadoras", "sintese_d_potencialidades_condicoes_facilitadoras", 0),
    SINTESE_E_PRINCIPAIS_DEMANDAS_IDENTIFICADAS("sinteseEPrincipaisDemandasIdentificadas", "sintese_e_principais_demandas_identificadas", 0),
    CHECKLIST_ACESSIBILIDADE_AMBIENTE("checklistAcessibilidadeAmbiente", "checklist_acessibilidade_ambiente", 0),
    CHECKLIST_COMUNICACAO_MATERIAIS("checklistComunicacaoMateriais", "checklist_comunicacao_materiais", 0),
    CHECKLIST_METODOLOGIA("checklistMetodologia", "checklist_metodologia", 0),
    CHECKLIST_AVALIACAO("checklistAvaliacao", "checklist_avaliacao", 0),
    CHECKLIST_CURRICULO("checklistCurriculo", "checklist_curriculo", 0),
    CHECKLIST_APOIOS_INSTITUCIONAIS("checklistApoiosInstitucionais", "checklist_apoios_institucionais", 0),
    APOIOS_EXTERNOS_ENCAMINHAMENTOS("apoiosExternosEncaminhamentos", "apoios_externos_encaminhamentos", 0),
    ASSINATURA_PSICOPEDAGOGO("assinaturaPsicopedagogo", "assinatura_psicopedagogo", 0),
    ASSINATURA_COORDENADOR_CLAI("assinaturaCoordenadorClai", "assinatura_coordenador_clai", 0);

    private final String chave;
    private final String coluna;
    private final int limite;

    CampoEstudoCaso(String chave, String coluna, int limite) {
        this.chave = chave;
        this.coluna = coluna;
        this.limite = limite;
    }

    public String chave() { return chave; }
    public String coluna() { return coluna; }
    public int limite() { return limite; }

    public static Optional<CampoEstudoCaso> porChave(String chave) {
        return Arrays.stream(values()).filter(campo -> campo.chave.equals(chave)).findFirst();
    }
}
