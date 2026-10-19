"use strict";

const mensagem = document.querySelector("#mensagem");
const cadastro = document.querySelector("#cadastro");
const dadosCadastrais = document.querySelector("#dados-cadastrais");
const historico = document.querySelector("#historico");
const estudos = document.querySelector("#estudos");
const semEstudos = document.querySelector("#sem-estudos");
const tentarNovamente = document.querySelector("#tentar-novamente");
const matricula = new URLSearchParams(location.search).get("matricula");
const editarCadastro = document.getElementById("editar-cadastro");
editarCadastro.hidden = !matricula;
if (matricula) editarCadastro.href = `/editar-estudante.html?matricula=${encodeURIComponent(matricula)}`;

function adicionarCampo(lista, titulo, valor) {
    const termo = document.createElement("dt");
    termo.textContent = titulo;
    const descricao = document.createElement("dd");
    descricao.textContent = valor == null || valor === "" ? "Não informado" : valor;
    lista.append(termo, descricao);
}

function exibirEstudante(estudante) {
    [
        ["Matrícula", estudante.matricula],
        ["Nome completo", estudante.nomeEstudante],
        ["Curso", estudante.curso?.nomeCurso],
        ["Turma", estudante.turma],
        ["Período de ingresso", estudante.periodoIngresso],
        ["Data de nascimento / idade", estudante.dataNascimentoIdade],
        ["Telefone do estudante", estudante.telefoneEstudante],
        ["E-mail do estudante", estudante.emailEstudante],
        ["Nome do responsável", estudante.nomeResponsavel],
        ["Grau de parentesco", estudante.grauParentesco],
        ["Telefone do responsável", estudante.telefoneResponsavel],
        ["E-mail do responsável", estudante.emailResponsavel]
    ].forEach(([titulo, valor]) => adicionarCampo(dadosCadastrais, titulo, valor));

    semEstudos.hidden = estudante.estudosDeCaso.length !== 0;
    estudante.estudosDeCaso.forEach(estudo => {
        const item = document.createElement("details");
        const titulo = document.createElement("summary");
        titulo.textContent = `Estudo de caso ${estudo.idEstudoCaso} — ${estudo.periodoLetivo}`;
        const dados = document.createElement("dl");
        [
            ["Identificador", estudo.idEstudoCaso],
            ["Período letivo", estudo.periodoLetivo],
            ["Datas de atendimento / anamnese", estudo.datasAtendimentoAnamnese],
            ["Origem do encaminhamento", estudo.origemEncaminhamento],
            ["Outra origem", estudo.origemEncaminhamentoOutro],
            ["Motivo principal", estudo.motivoPrincipal],
            ["Demanda do estudante", estudo.demandaEstudante],
            ["Demanda da família / responsável", estudo.demandaFamiliaResponsavel],
            ["Demanda da equipe escolar", estudo.demandaEquipeEscolar],
            ["Data de fechamento", estudo.dataFechamento?.split("-").reverse().join("/")]
        ].forEach(([rotulo, valor]) => adicionarCampo(dados, rotulo, valor));
        const documentos = document.createElement("a");
        documentos.href = `/documentos-estudo-caso.html?id=${encodeURIComponent(estudo.idEstudoCaso)}`;
        documentos.textContent = "Laudos e documentos";
        item.append(titulo, dados, documentos);
        estudos.append(item);
    });
    cadastro.hidden = false;
    historico.hidden = false;
    mensagem.textContent = "Dados carregados.";
}

async function carregar() {
    cadastro.hidden = true;
    historico.hidden = true;
    tentarNovamente.hidden = true;
    dadosCadastrais.replaceChildren();
    estudos.replaceChildren();
    if (!matricula?.trim()) {
        mensagem.textContent = "Selecione um estudante na página de consulta.";
        return;
    }
    mensagem.textContent = "Carregando dados…";
    try {
        const resposta = await fetch(`/api/estudantes/${encodeURIComponent(matricula)}`, {
            headers: { Accept: "application/json" },
            cache: "no-store"
        });
        if (resposta.status === 404) {
            mensagem.textContent = "Estudante não encontrado.";
            return;
        }
        if (!resposta.ok) throw new Error();
        const estudante = await resposta.json();
        if (!Array.isArray(estudante.estudosDeCaso)) throw new Error();
        exibirEstudante(estudante);
    } catch {
        cadastro.hidden = true;
        historico.hidden = true;
        mensagem.textContent = "Não foi possível carregar os dados do estudante. Verifique a conexão e tente novamente.";
        tentarNovamente.hidden = false;
    }
}

tentarNovamente.addEventListener("click", carregar);
carregar();
