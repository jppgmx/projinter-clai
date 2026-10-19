CREATE TABLE IF NOT EXISTS curso (
    id_curso SERIAL PRIMARY KEY,
    nome_curso VARCHAR(150) NOT NULL UNIQUE
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

-- Projeção do schema existente necessária apenas aos testes de consulta (T07).
CREATE TABLE IF NOT EXISTS estudo_de_caso (
    id_estudo_caso SERIAL PRIMARY KEY,
    fk_matricula_estudante VARCHAR(30) NOT NULL REFERENCES estudante(matricula) ON DELETE CASCADE,
    periodo_letivo VARCHAR(20) NOT NULL,
    datas_atendimento_anamnese TEXT NOT NULL,
    origem_encaminhamento VARCHAR(50) NOT NULL,
    origem_encaminhamento_outro VARCHAR(150),
    motivo_principal TEXT NOT NULL,
    demanda_estudante TEXT,
    demanda_familia_responsavel TEXT,
    demanda_equipe_escolar TEXT,
    data_fechamento DATE,
    arquivado BOOLEAN NOT NULL DEFAULT FALSE
);
