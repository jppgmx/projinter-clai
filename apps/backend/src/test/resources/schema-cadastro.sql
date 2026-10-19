-- Apenas fixture dos testes opcionais de integração; não é carregada pela aplicação.

CREATE TABLE IF NOT EXISTS setor (
    id_setor SERIAL PRIMARY KEY,
    sigla_nome VARCHAR(100) NOT NULL,
    sigla_nome_outro VARCHAR(150),
    campus VARCHAR(150) NOT NULL DEFAULT 'IFPB - Campus Cajazeiras',
    CONSTRAINT chk_setor_sigla CHECK (
        sigla_nome IN (
            'CLAI', 'COPED', 'CAEST', 'Coordenação de Curso', 
            'Setor Pedagógico', 'Docente', 'Outro'
        )
    ),
    CONSTRAINT chk_setor_outro CHECK (
        sigla_nome <> 'Outro' OR sigla_nome_outro IS NOT NULL
    )
);

CREATE TABLE IF NOT EXISTS curso (
    id_curso SERIAL PRIMARY KEY,
    nome_curso VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS profissional (
    id_profissional SERIAL PRIMARY KEY,
    fk_id_setor INTEGER NOT NULL,
    nome_profissional VARCHAR(150) NOT NULL,
    papel_cargo VARCHAR(100) NOT NULL,
    papel_cargo_outro VARCHAR(150),
    CONSTRAINT fk_profissional_setor 
        FOREIGN KEY (fk_id_setor) REFERENCES setor (id_setor) ON DELETE RESTRICT,
    CONSTRAINT chk_profissional_papel CHECK (
        papel_cargo IN (
            'Docente', 'Psicopedagogo(a)', 'Coordenador(a) da CLAI', 
            'Coordenador(a) de Curso', 'Responsável Setor Pedagógico', 
            'Técnico CLAI', 'Assistente Inclusivo', 'Monitor', 'Cuidador', 'Outro'
        )
    ),
    CONSTRAINT chk_profissional_papel_outro CHECK (
        papel_cargo <> 'Outro' OR papel_cargo_outro IS NOT NULL
    )
);

CREATE TABLE IF NOT EXISTS estudante (
    matricula VARCHAR(30) PRIMARY KEY,
    fk_id_curso INTEGER NOT NULL,
    turma VARCHAR(50) NOT NULL,
    nome_estudante VARCHAR(150) NOT NULL,
    periodo_ingresso VARCHAR(20) NOT NULL,
    data_nascimento_idade VARCHAR(60) NOT NULL, -- Ex.: '15/04/2005 (21 anos)'
    telefone_estudante VARCHAR(30),
    email_estudante VARCHAR(150),
    nome_responsavel VARCHAR(150),
    grau_parentesco VARCHAR(60),
    telefone_responsavel VARCHAR(30),
    email_responsavel VARCHAR(150),
    CONSTRAINT fk_estudante_curso 
        FOREIGN KEY (fk_id_curso) REFERENCES curso (id_curso) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS estudo_de_caso (
    id_estudo_caso SERIAL PRIMARY KEY,
    fk_matricula_estudante VARCHAR(30) NOT NULL,
    fk_profissional_responsavel INTEGER NOT NULL,
    fk_psicopedagogo_assinatura INTEGER,
    fk_coordenador_clai_assinatura INTEGER,

    -- Seção 1: Dados do Atendimento
    periodo_letivo VARCHAR(20) NOT NULL,
    datas_atendimento_anamnese TEXT NOT NULL,

    -- Seção 2: Motivo do Encaminhamento e Demanda Inicial
    origem_encaminhamento VARCHAR(50) NOT NULL,
    origem_encaminhamento_outro VARCHAR(150),
    motivo_principal TEXT NOT NULL,
    demanda_estudante TEXT,
    demanda_familia_responsavel TEXT,
    demanda_equipe_escolar TEXT,

    -- Seção 3: Dados sobre a Necessidade Específica e Documentação (Flag + Quais achatados)
    deficiencia_necessidade_informada TEXT NOT NULL,
    diagnosticos TEXT,
    docs_comprobatorios_flag_quais TEXT NOT NULL, -- Ex.: 'Não' ou 'Sim: Laudo Neurológico CID...'
    data_profissional_docs TEXT,
    condicao_associada_flag_quais TEXT NOT NULL,  -- Ex.: 'Não' ou 'Sim: TDAH / Ansiedade'
    medicacao_continua_flag_quais TEXT NOT NULL,  -- Ex.: 'Não' ou 'Sim: Ritalina 10mg'
    encaminhamentos_anteriores TEXT,
    servicos_apoio_atuais_anteriores TEXT,

    -- Seção 4: Anamnese - Histórico de Desenvolvimento e Vida Escolar
    historico_escolar TEXT,
    experiencias_escolares_positivas TEXT,
    experiencias_negativas_barreiras TEXT,
    adaptacoes_recursos_utilizados_resultados TEXT,
    objetivos_conhecimentos_consolidados TEXT,
    areas_disciplinas_maior_facilidade TEXT,
    areas_disciplinas_maior_dificuldade TEXT,

    -- Seção 5: Perfil de Aprendizagem, Comunicação, Participação e Autonomia
    comunicacao_compreensao_orientacoes TEXT,
    leitura_escrita_linguagem TEXT,
    raciocinio_logico_matematico TEXT,
    atencao_memoria_funcoes_executivas TEXT,
    organizacao_planejamento_tarefas TEXT,
    interacao_social_participacao_sala TEXT,
    aspectos_sensoriais_relevantes TEXT,
    mobilidade_acesso_fisico_tecnologias TEXT,
    autonomia_contexto_familiar TEXT,
    autonomia_contexto_escolar TEXT,
    estrategias_favorecem_aprendizagem TEXT,
    situacoes_dificultam_desorganizam_aprendizagem TEXT,

    -- Seção 6: Contexto Psicossocial e Escolha/Pertencimento ao Curso
    relacao_curso_expectativas TEXT,
    rede_apoio_familiar_social TEXT,
    conflitos_fatores_psicossociais TEXT,
    observacoes_frequencia_rotina_permanencia TEXT,

    -- Seção 7: Potencialidades, Interesses e Recursos do(a) Estudante
    potencialidades_academicas TEXT,
    interesses_habilidades TEXT,
    formas_preferenciais_aprender TEXT,
    recursos_pessoais_tecnologicos TEXT,

    -- Seção 9: Síntese Psicopedagógica do Estudo de Caso
    sintese_a_identificacao_necessidades TEXT,
    sintese_b_avancos_objetivos_alcancados TEXT,
    sintese_c_dificuldades_barreiras_educacionais TEXT,
    sintese_d_potencialidades_condicoes_facilitadoras TEXT,
    sintese_e_principais_demandas_identificadas TEXT,

    -- Seção 10: Checklist de Apoios/Adaptações a Considerar e Assinaturas
    checklist_acessibilidade_ambiente TEXT,
    checklist_comunicacao_materiais TEXT,
    checklist_metodologia TEXT,
    checklist_avaliacao TEXT,
    checklist_curriculo TEXT,
    checklist_apoios_institucionais TEXT,
    apoios_externos_encaminhamentos TEXT,
    assinatura_psicopedagogo TEXT,
    assinatura_coordenador_clai TEXT,
    data_fechamento DATE,

    CONSTRAINT fk_ec_estudante 
        FOREIGN KEY (fk_matricula_estudante) REFERENCES estudante (matricula) ON DELETE CASCADE,
    CONSTRAINT fk_ec_prof_responsavel 
        FOREIGN KEY (fk_profissional_responsavel) REFERENCES profissional (id_profissional) ON DELETE RESTRICT,
    CONSTRAINT fk_ec_psicopedagogo 
        FOREIGN KEY (fk_psicopedagogo_assinatura) REFERENCES profissional (id_profissional) ON DELETE SET NULL,
    CONSTRAINT fk_ec_coordenador_clai 
        FOREIGN KEY (fk_coordenador_clai_assinatura) REFERENCES profissional (id_profissional) ON DELETE SET NULL,
    CONSTRAINT chk_ec_origem_encaminhamento CHECK (
        origem_encaminhamento IN (
            'CLAI', 'COPED', 'CAEST', 'Coordenação de Curso', 
            'Docente', 'Família', 'Estudante', 'Outro'
        )
    ),
    CONSTRAINT chk_ec_origem_outro CHECK (
        origem_encaminhamento <> 'Outro' OR origem_encaminhamento_outro IS NOT NULL
    )
);

-- ============================================================================
-- MIGRAÇÃO: arquivar / desarquivar Estudo de Caso
-- Rode UMA vez no SQL Editor do Supabase ANTES de subir a nova versão do app.
-- Estudos já existentes ficam com arquivado = FALSE (DEFAULT).
-- ============================================================================
ALTER TABLE estudo_de_caso
    ADD COLUMN IF NOT EXISTS arquivado BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE estudo_de_caso
    ADD COLUMN IF NOT EXISTS data_arquivamento TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_estudo_caso_arquivado ON estudo_de_caso (arquivado);

-- Somente fixture de testes: permite verificar o bloqueio da T11 quando outra US já trancou o estudo.
ALTER TABLE estudo_de_caso ADD COLUMN IF NOT EXISTS trancado BOOLEAN NOT NULL DEFAULT FALSE;
