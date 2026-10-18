CREATE TABLE curso (
    id_curso SERIAL PRIMARY KEY,
    nome_curso VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE estudante (
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