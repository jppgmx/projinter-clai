"use strict";
const camposTexto = ["periodoLetivo", "datasAtendimentoAnamnese", "origemEncaminhamento", "origemEncaminhamentoOutro", "motivoPrincipal", "demandaEstudante", "demandaFamiliaResponsavel", "demandaEquipeEscolar", "deficienciaNecessidadeInformada", "diagnosticos", "docsComprobatoriosFlagQuais", "dataProfissionalDocs", "condicaoAssociadaFlagQuais", "medicacaoContinuaFlagQuais", "encaminhamentosAnteriores", "servicosApoioAtuaisAnteriores", "historicoEscolar", "experienciasEscolaresPositivas", "experienciasNegativasBarreiras", "adaptacoesRecursosUtilizadosResultados", "objetivosConhecimentosConsolidados", "areasDisciplinasMaiorFacilidade", "areasDisciplinasMaiorDificuldade", "comunicacaoCompreensaoOrientacoes", "leituraEscritaLinguagem", "raciocinioLogicoMatematico", "atencaoMemoriaFuncoesExecutivas", "organizacaoPlanejamentoTarefas", "interacaoSocialParticipacaoSala", "aspectosSensoriaisRelevantes", "mobilidadeAcessoFisicoTecnologias", "autonomiaContextoFamiliar", "autonomiaContextoEscolar", "estrategiasFavorecemAprendizagem", "situacoesDificultamDesorganizamAprendizagem", "relacaoCursoExpectativas", "redeApoioFamiliarSocial", "conflitosFatoresPsicossociais", "observacoesFrequenciaRotinaPermanencia", "potencialidadesAcademicas", "interessesHabilidades", "formasPreferenciaisAprender", "recursosPessoaisTecnologicos", "sinteseAIdentificacaoNecessidades", "sinteseBAvancosObjetivosAlcancados", "sinteseCDificuldadesBarreirasEducacionais", "sinteseDPotencialidadesCondicoesFacilitadoras", "sinteseEPrincipaisDemandasIdentificadas", "checklistAcessibilidadeAmbiente", "checklistComunicacaoMateriais", "checklistMetodologia", "checklistAvaliacao", "checklistCurriculo", "checklistApoiosInstitucionais", "apoiosExternosEncaminhamentos", "assinaturaPsicopedagogo", "assinaturaCoordenadorClai"];
const camposProfissionais = ["idProfissionalResponsavel", "idPsicopedagogoAssinatura", "idCoordenadorClaiAssinatura"];
const camposSimNao = ["docsComprobatoriosFlagQuais", "condicaoAssociadaFlagQuais", "medicacaoContinuaFlagQuais"];
const consulta = document.querySelector("#consulta-estudo");
const numero = document.querySelector("#id-estudo");
const carregar = document.querySelector("#carregar");
const form = document.querySelector("#edicao");
const fieldset = document.querySelector("#campos-edicao");
const salvar = document.querySelector("#salvar");
const mensagem = document.querySelector("#mensagem");
let atual;
let requisicao;
let salvando = false;
let recarregarNecessario = false;

function informar(texto) {
    mensagem.textContent = texto;
    mensagem.focus();
}

function limparErros() {
    form.querySelectorAll('[id^="erro-"]').forEach(campo => { campo.textContent = ""; });
    form.querySelectorAll("input, select, textarea").forEach(campo => {
        campo.removeAttribute("aria-invalid");
        campo.setCustomValidity("");
    });
}

function configurarOrigem() {
    const outro = document.getElementById("origemEncaminhamento").value === "Outro";
    document.querySelector("#campo-outra-origem").hidden = !outro;
    const campo = document.getElementById("origemEncaminhamentoOutro");
    campo.required = outro;
    if (!outro) {
        campo.value = "";
        campo.setCustomValidity("");
    }
}

function preencher(estudo, profissionais) {
    camposTexto.forEach(chave => { document.getElementById(chave).value = estudo.campos[chave] ?? ""; });
    camposProfissionais.forEach(chave => {
        const select = document.getElementById(chave);
        select.replaceChildren(new Option(chave === "idProfissionalResponsavel" ? "Selecione" : "Não informado", ""));
        profissionais.forEach(item => select.add(new Option(`${item.nomeProfissional} — ${item.papelCargo}`, item.idProfissional)));
        const id = estudo.profissionais[chave];
        if (id != null && !profissionais.some(item => item.idProfissional === id)) {
            select.add(new Option(`Profissional ${id} (indisponível)`, id));
        }
        select.value = id ?? "";
    });
    atual = estudo;
    limparErros();
    configurarOrigem();
    document.querySelector("#identificacao").hidden = false;
    document.querySelector("#titulo-estudo").textContent = `Estudo de caso ${estudo.idEstudoCaso}`;
    document.querySelector("#estudante").textContent = `${estudo.nomeEstudante} — Matrícula ${estudo.matriculaEstudante}`;
    document.querySelector("#situacao").textContent = estudo.editavel ? "Em andamento — disponível para edição." : estudo.motivoBloqueio;
    form.hidden = false;
    fieldset.disabled = !estudo.editavel;
    salvar.hidden = !estudo.editavel;
    salvar.disabled = !estudo.editavel;
}

async function obter(url, signal) {
    const response = await fetch(url, { signal, cache: "no-store", headers: { Accept: "application/json" } });
    const body = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(body.erro || "Não foi possível carregar os dados.");
    return body;
}

async function carregarEstudo() {
    if (salvando || !consulta.reportValidity()) return;
    const id = Number(numero.value);
    if (!Number.isSafeInteger(id) || id < 1) return;
    requisicao?.abort();
    const controller = new AbortController();
    requisicao = controller;
    form.hidden = true;
    document.querySelector("#identificacao").hidden = true;
    carregar.disabled = true;
    atual = null;
    recarregarNecessario = false;
    mensagem.textContent = "Carregando estudo…";
    try {
        const [estudo, profissionais] = await Promise.all([
            obter(`/api/estudos-caso/${id}`, controller.signal),
            obter("/api/estudos-caso/profissionais", controller.signal)
        ]);
        if (controller.signal.aborted) return;
        if (!estudo.campos || !estudo.profissionais || !Array.isArray(profissionais)) throw new Error("Resposta inválida ao carregar o estudo.");
        preencher(estudo, profissionais);
        history.replaceState(null, "", `${location.pathname}?id=${id}`);
        informar(estudo.editavel ? "Dados carregados. Altere os campos necessários e salve." : estudo.motivoBloqueio);
    } catch (erro) {
        if (erro.name !== "AbortError") informar(erro.message || "Verifique a conexão e tente carregar novamente.");
    } finally {
        if (!controller.signal.aborted) carregar.disabled = false;
    }
}

consulta.addEventListener("submit", event => { event.preventDefault(); carregarEstudo(); });
document.getElementById("origemEncaminhamento").addEventListener("change", configurarOrigem);
form.addEventListener("input", event => {
    const campo = event.target;
    campo.removeAttribute("aria-invalid");
    campo.setCustomValidity("");
    const erro = document.getElementById(`erro-${campo.id}`);
    if (erro) erro.textContent = "";
    if (campo.required && !campo.value.trim()) campo.setCustomValidity("Preencha este campo.");
    if (camposSimNao.includes(campo.id) && /^sim[\s:–—,;-]*$/i.test(campo.value.trim())) {
        campo.setCustomValidity("Se informar Sim, descreva quais.");
    }
});

form.addEventListener("submit", async event => {
    event.preventDefault();
    if (salvando || !atual?.editavel || recarregarNecessario || !form.reportValidity()) return;
    const campos = {};
    const profissionais = {};
    camposTexto.forEach(chave => {
        const valor = document.getElementById(chave).value;
        if (valor !== (atual.campos[chave] ?? "")) campos[chave] = valor;
    });
    camposProfissionais.forEach(chave => {
        const valor = document.getElementById(chave).value;
        const id = valor ? Number(valor) : null;
        if (id !== atual.profissionais[chave]) profissionais[chave] = id;
    });
    if (!Object.keys(campos).length && !Object.keys(profissionais).length) {
        informar("Não há alterações para salvar.");
        return;
    }
    limparErros();
    salvando = true;
    salvar.disabled = true;
    fieldset.disabled = true;
    carregar.disabled = true;
    numero.disabled = true;
    salvar.textContent = "Salvando…";
    try {
        const response = await fetch(`/api/estudos-caso/${atual.idEstudoCaso}`, {
            method: "PUT", headers: { "Content-Type": "application/json", Accept: "application/json" },
            body: JSON.stringify({ campos, profissionais })
        });
        const body = await response.json().catch(() => ({}));
        if (!response.ok) {
            recarregarNecessario = response.status === 409 || response.status === 404 || response.status >= 500;
            fieldset.disabled = recarregarNecessario;
            let primeiro;
            Object.entries(body.campos || {}).forEach(([chave, texto]) => {
                const campo = document.getElementById(chave);
                const erro = document.getElementById(`erro-${chave}`);
                if (campo && erro) {
                    campo.setAttribute("aria-invalid", "true");
                    erro.textContent = texto;
                    primeiro ||= campo;
                }
            });
            informar((body.erro || "Não foi possível salvar as alterações.") + (recarregarNecessario ? " Carregue o estudo novamente antes de reenviar." : ""));
            primeiro?.focus();
            return;
        }
        atual = body;
        camposTexto.forEach(chave => { document.getElementById(chave).value = body.campos[chave] ?? ""; });
        camposProfissionais.forEach(chave => { document.getElementById(chave).value = body.profissionais[chave] ?? ""; });
        configurarOrigem();
        informar("Estudo de caso atualizado com sucesso.");
    } catch {
        recarregarNecessario = true;
        informar("Não foi possível confirmar a atualização. Os dados foram mantidos na tela. Carregue o estudo novamente para conferir o que foi salvo antes de reenviar.");
    } finally {
        salvando = false;
        fieldset.disabled = recarregarNecessario || !atual?.editavel;
        salvar.disabled = recarregarNecessario || !atual?.editavel;
        carregar.disabled = false;
        numero.disabled = false;
        salvar.textContent = "Salvar alterações";
    }
});
const idInicial = new URLSearchParams(location.search).get("id");
if (idInicial) { numero.value = idInicial; carregarEstudo(); }
