"use strict";
(() => {
    const filtro = document.getElementById("filtro-estudos");
    const matricula = document.getElementById("matricula-filtro");
    const mensagem = document.getElementById("mensagem-listagem");
    const tabela = document.getElementById("lista-estudos");
    const linhas = document.getElementById("linhas-estudos");
    const selecionarSituacao = tabela.dataset.selecao === "situacao";
    const selecionarDocumentos = tabela.dataset.selecao === "documentos";
    const situacoes = { EM_ANDAMENTO: "Em andamento", ARQUIVADO: "Arquivado",
        TRANCADO: "Trancado", FINALIZADO: "Finalizado" };
    let requisicao;

    async function listar() {
        requisicao?.abort();
        const controller = new AbortController();
        requisicao = controller;
        const valor = matricula.value.trim();
        const parametros = new URLSearchParams();
        if (valor) parametros.set("matricula", valor);
        tabela.hidden = true;
        linhas.replaceChildren();
        mensagem.textContent = "Carregando estudos de caso…";
        try {
            const response = await fetch(`/api/estudos-caso${valor ? `?${parametros}` : ""}`, {
                signal: controller.signal, cache: "no-store", headers: { Accept: "application/json" }
            });
            if (!response.ok) throw new Error("Não foi possível consultar os estudos de caso. Tente novamente.");
            const estudos = await response.json();
            if (controller.signal.aborted) return;
            if (!Array.isArray(estudos)) throw new Error("Resposta inválida ao consultar os estudos de caso.");
            const fragmento = document.createDocumentFragment();
            estudos.forEach(estudo => {
                const linha = document.createElement("tr");
                [estudo.idEstudoCaso, estudo.matriculaEstudante, estudo.nomeEstudante,
                    estudo.periodoLetivo, situacoes[estudo.status] ?? estudo.status].forEach(valor => {
                    const celula = document.createElement("td");
                    celula.textContent = valor ?? "Não informado";
                    if (!selecionarDocumentos) {
                    const documentos = document.createElement("a");
                    documentos.href = `/documentos-estudo-caso.html?id=${encodeURIComponent(estudo.idEstudoCaso)}`;
                    documentos.textContent = "Laudos e documentos";
                    celula.append(document.createTextNode(" | "), documentos);
                }
                linha.append(celula);
                });
                const celula = document.createElement("td");
                const link = document.createElement("a");
                link.href = `${selecionarSituacao ? "situacao-estudo-caso" : selecionarDocumentos ? "documentos-estudo-caso" : "editar-estudo-caso"}.html?id=${encodeURIComponent(estudo.idEstudoCaso)}`;
                link.textContent = selecionarSituacao ? "Selecionar estudo" : selecionarDocumentos ? "Ver documentos" : estudo.editavel ? "Editar" : "Visualizar";
                if (selecionarSituacao) {
                    link.addEventListener("click", event => {
                        if (event.button !== 0 || event.ctrlKey || event.metaKey || event.shiftKey || event.altKey) return;
                        event.preventDefault();
                        document.dispatchEvent(new CustomEvent("clai:selecionar-situacao", { detail: estudo.idEstudoCaso }));
                    });
                }
                link.setAttribute("aria-label", `${link.textContent} estudo de caso ${estudo.idEstudoCaso}`);
                celula.append(link);
                if (selecionarSituacao) {
                    const editar = document.createElement("a");
                    editar.href = `editar-estudo-caso.html?id=${encodeURIComponent(estudo.idEstudoCaso)}`;
                    editar.textContent = estudo.editavel ? "Atualizar estudo" : "Visualizar estudo";
                    editar.setAttribute("aria-label", `${editar.textContent}: ${estudo.idEstudoCaso}`);
                    celula.append(document.createTextNode(" | "), editar);
                }
                if (!selecionarSituacao) {
                    celula.append(document.createTextNode(" | "));
                    const situacao = document.createElement("a");
                    situacao.href = `situacao-estudo-caso.html?id=${encodeURIComponent(estudo.idEstudoCaso)}`;
                    situacao.textContent = "Arquivamento e trancamento";
                    situacao.setAttribute("aria-label", `Consultar arquivamento e trancamento do estudo ${estudo.idEstudoCaso}`);
                    celula.append(situacao);
                }
                if (!selecionarDocumentos) {
                    const documentos = document.createElement("a");
                    documentos.href = `/documentos-estudo-caso.html?id=${encodeURIComponent(estudo.idEstudoCaso)}`;
                    documentos.textContent = "Laudos e documentos";
                    celula.append(document.createTextNode(" | "), documentos);
                }
                linha.append(celula);
                fragmento.append(linha);
            });
            linhas.append(fragmento);
            tabela.hidden = estudos.length === 0;
            mensagem.textContent = estudos.length
                ? `${estudos.length} estudo(s) encontrado(s).${valor ? ` Matrícula: ${valor}.` : " Exibindo todos."}`
                : valor ? "Nenhum estudo encontrado para essa matrícula." : "Nenhum estudo de caso cadastrado.";
        } catch (erro) {
            if (erro.name !== "AbortError" && !controller.signal.aborted) {
                mensagem.textContent = erro.message || "Verifique a conexão e tente novamente.";
            }
        }
    }

    filtro.addEventListener("submit", event => { event.preventDefault(); listar(); });
    document.getElementById("todos-estudos").addEventListener("click", () => {
        matricula.value = "";
        listar();
    });
    document.addEventListener("clai:situacao-alterada", listar);
    listar();
})();
