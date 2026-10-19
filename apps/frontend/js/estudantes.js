"use strict";

const formulario = document.querySelector("#consulta");
const busca = document.querySelector("#busca");
const mensagem = document.querySelector("#mensagem");
const tabela = document.querySelector("#resultado");
const corpo = document.querySelector("#estudantes");
let requisicao;

async function consultar() {
    requisicao?.abort();
    const atual = new AbortController();
    requisicao = atual;
    tabela.hidden = true;
    corpo.replaceChildren();
    mensagem.textContent = "Carregando estudantes…";
    const termo = busca.value.trim();
    const parametros = new URLSearchParams();
    if (termo) parametros.set("busca", termo);
    const query = parametros.size ? `?${parametros}` : "";
    history.replaceState(null, "", `${location.pathname}${query}`);

    try {
        const resposta = await fetch(`/api/estudantes${query}`, {
            signal: atual.signal,
            headers: { Accept: "application/json" },
            cache: "no-store"
        });
        if (!resposta.ok) throw new Error();
        const estudantes = await resposta.json();
        if (!Array.isArray(estudantes)) throw new Error();
        if (atual.signal.aborted) return;

        estudantes.forEach(estudante => {
            const linha = document.createElement("tr");
            [estudante.matricula, estudante.nomeEstudante, estudante.curso?.nomeCurso,
                estudante.turma, estudante.periodoIngresso].forEach(valor => {
                const coluna = document.createElement("td");
                coluna.textContent = valor ?? "Não informado";
                linha.append(coluna);
            });
            const coluna = document.createElement("td");
            const link = document.createElement("a");
            link.href = `detalhes-estudante.html?matricula=${encodeURIComponent(estudante.matricula)}`;
            link.textContent = "Ver dados e histórico";
            link.setAttribute("aria-label", `Ver dados e histórico de ${estudante.nomeEstudante}`);
            const editar = document.createElement("a");
            editar.href = `/editar-estudante.html?matricula=${encodeURIComponent(estudante.matricula)}`;
            editar.textContent = "Editar cadastro";
            coluna.append(link, document.createTextNode(" | "), editar);
            linha.append(coluna);
            corpo.append(linha);
        });
        tabela.hidden = estudantes.length === 0;
        mensagem.textContent = estudantes.length
            ? `${estudantes.length} estudante(s) encontrado(s).`
            : "Nenhum estudante encontrado.";
    } catch (erro) {
        if (erro.name === "AbortError") return;
        mensagem.textContent = "Não foi possível consultar os estudantes. Verifique a conexão e clique em Buscar para tentar novamente.";
    }
}

formulario.addEventListener("submit", evento => {
    evento.preventDefault();
    consultar();
});
busca.value = new URLSearchParams(location.search).get("busca") || "";
consultar();
