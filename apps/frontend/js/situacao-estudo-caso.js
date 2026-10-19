"use strict";
(() => {
    const consulta = document.getElementById("consulta-situacao");
    const numero = document.getElementById("id-estudo");
    const carregar = document.getElementById("carregar");
    const dados = document.getElementById("dados");
    const mensagem = document.getElementById("mensagem");
    const arquivamento = document.getElementById("arquivamento");
    const trancamento = document.getElementById("trancamento");
    const motivo = document.getElementById("motivo");
    const justificativa = document.getElementById("justificativa");
    let estudo;
    let ocupado = false;
    let exigeRecarga = false;

    function informar(texto) {
        mensagem.textContent = texto;
        mensagem.focus();
    }

    function habilitar() {
        document.getElementById("desarquivar").disabled = ocupado || exigeRecarga || !estudo?.arquivado;
        carregar.disabled = ocupado;
        numero.disabled = ocupado;
        document.getElementById("campos-arquivar").disabled = ocupado || exigeRecarga || !estudo || estudo.arquivado;
        document.getElementById("campos-trancar").disabled = ocupado || exigeRecarga || !estudo || estudo.arquivado || estudo.trancado || !!estudo.dataFechamento;
    }

    function mostrar(atual) {
        estudo = atual;
        dados.hidden = false;
        const texto = (id, valor) => { document.getElementById(id).textContent = valor ?? "Não informado"; };
        texto("titulo-estudo", `Estudo de caso ${atual.idEstudoCaso}`);
        texto("matricula", `Matrícula: ${atual.matriculaEstudante}`);
        const estados = [];
        if (atual.arquivado) estados.push("Arquivado");
        if (atual.trancado) estados.push("Trancado");
        if (atual.dataFechamento) estados.push("Finalizado");
        texto("situacao", estados.join(" / ") || "Em andamento");
        texto("data-fechamento", atual.dataFechamento);
        texto("data-arquivamento", atual.dataArquivamento?.replace("T", " "));
        texto("motivo-salvo", atual.motivoArquivamento);
        texto("data-trancamento", atual.dataTrancamento?.replace("T", " "));
        texto("justificativa-salva", atual.justificativaTrancamento);
        texto("bloqueio-arquivar", atual.arquivado ? "Este estudo já está arquivado." : "");
        texto("bloqueio-trancar", atual.arquivado ? "Estudo arquivado não pode ser trancado."
            : atual.trancado ? "Este estudo já está trancado."
            : atual.dataFechamento ? "Estudo finalizado não pode ser trancado." : "");
        const bloqueado = atual.arquivado || atual.trancado || !!atual.dataFechamento;
        const linkEdicao = document.getElementById("visualizar");
        linkEdicao.href = `editar-estudo-caso.html?id=${encodeURIComponent(atual.idEstudoCaso)}`;
        linkEdicao.textContent = bloqueado ? "Visualizar estudo (edição bloqueada)" : "Atualizar estudo de caso";
        texto("orientacao-edicao", atual.arquivado ? "Desarquive para remover o bloqueio de arquivamento. Outros bloqueios, como trancamento ou fechamento, permanecem."
            : atual.trancado || atual.dataFechamento ? "A edição permanece bloqueada pelo trancamento ou fechamento."
            : "Abra Atualizar estudo de caso, altere os campos e clique em Salvar alterações.");
        habilitar();
    }

    async function carregarSituacao() {
        if (ocupado || !consulta.reportValidity()) return;
        const id = Number(numero.value);
        if (!Number.isSafeInteger(id) || id < 1) return;
        ocupado = true;
        exigeRecarga = false;
        estudo = null;
        dados.hidden = true;
        habilitar();
        informar("Carregando situação…");
        try {
            const response = await fetch(`/api/estudos-caso/${id}/situacao`, {
                cache: "no-store", headers: { Accept: "application/json" }
            });
            const body = await response.json().catch(() => ({}));
            if (!response.ok) throw new Error(body.erro || "Não foi possível carregar a situação.");
            if (body.idEstudoCaso !== id) throw new Error("Resposta inválida ao carregar a situação.");
            mostrar(body);
            arquivamento.reset();
            trancamento.reset();
            motivo.setCustomValidity("");
            justificativa.setCustomValidity("");
            history.replaceState(null, "", `${location.pathname}?id=${id}`);
            informar("Situação carregada. Informe o motivo ou a justificativa para a ação desejada.");
        } catch (erro) {
            informar(erro.message || "Verifique a conexão e carregue novamente.");
        } finally {
            ocupado = false;
            habilitar();
        }
    }

    async function executar(form, campo, acao, chave) {
        if (ocupado || exigeRecarga || !estudo) return;
        if (acao === "arquivar" && estudo.arquivado) return;
        if (acao === "trancar" && (estudo.arquivado || estudo.trancado || estudo.dataFechamento)) return;
        if (acao === "desarquivar" && !estudo.arquivado) return;
        if (campo) {
            campo.setCustomValidity(campo.value.trim() ? "" : "Preencha este campo.");
            if (!form.reportValidity()) return;
        }
        ocupado = true;
        habilitar();
        informar(acao === "arquivar" ? "Arquivando estudo…" : acao === "desarquivar" ? "Desarquivando estudo…" : "Trancando estudo…");
        try {
            const response = await fetch(`/api/estudos-caso/${estudo.idEstudoCaso}/${acao}`, {
                method: "PATCH", headers: { "Content-Type": "application/json", Accept: "application/json" },
                body: campo ? JSON.stringify({ [chave]: campo.value.trim() }) : undefined
            });
            const body = await response.json().catch(() => ({}));
            if (!response.ok) {
                exigeRecarga = response.status !== 400;
                informar((body.erro || "Não foi possível concluir a operação.")
                    + (exigeRecarga ? " Carregue a situação novamente antes de reenviar." : ""));
                return;
            }
            if (body.idEstudoCaso !== estudo.idEstudoCaso) throw new Error("Resposta inválida.");
            mostrar(body);
            form?.reset();
            informar(acao === "arquivar" ? "Estudo arquivado. Motivo e data registrados."
                : acao === "desarquivar" ? "Estudo desarquivado. Use Atualizar estudo de caso para editar, se não houver outro bloqueio."
                : "Estudo trancado. Justificativa e data registradas.");
            document.dispatchEvent(new Event("clai:situacao-alterada"));
        } catch {
            exigeRecarga = true;
            informar("Não foi possível confirmar a operação. Carregue a situação novamente para verificar o resultado antes de reenviar. O texto foi mantido na tela.");
        } finally {
            ocupado = false;
            habilitar();
        }
    }

    document.getElementById("desarquivamento").addEventListener("submit", event => {
        event.preventDefault();
        executar(null, null, "desarquivar", null);
    });
    consulta.addEventListener("submit", event => { event.preventDefault(); carregarSituacao(); });
    arquivamento.addEventListener("submit", event => { event.preventDefault(); executar(arquivamento, motivo, "arquivar", "motivo"); });
    trancamento.addEventListener("submit", event => { event.preventDefault(); executar(trancamento, justificativa, "trancar", "justificativa"); });
    [motivo, justificativa].forEach(campo => campo.addEventListener("input", () => campo.setCustomValidity("")));
    document.addEventListener("clai:selecionar-situacao", event => {
        if (ocupado) return;
        numero.value = event.detail;
        carregarSituacao();
    });
    const id = new URLSearchParams(location.search).get("id");
    if (id) { numero.value = id; carregarSituacao(); }
})();
