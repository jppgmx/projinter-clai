import { configurarBuscaEstudante } from "./busca-estudante.js";
import { configurarDatas } from "./campos-data.js";
const form = document.querySelector("#abertura");
const estudante = document.querySelector("#matriculaEstudante");
const buscaEstudante = document.querySelector("#buscaEstudante");
const busca = configurarBuscaEstudante(buscaEstudante, estudante);
configurarDatas("datasAtendimentoAnamnese", { multiplo: true });
const profissional = document.querySelector("#idProfissionalResponsavel");
const origem = document.querySelector("#origemEncaminhamento");
const outraOrigem = document.querySelector("#origemEncaminhamentoOutro");
const abrir = document.querySelector("#abrir");
const mensagem = document.querySelector("#mensagem");
const recarregar = document.querySelector("#recarregar");
const resultado = document.querySelector("#resultado");

function informar(texto) {
    mensagem.textContent = texto;
    mensagem.focus();
}

async function consultar(url) {
    const response = await fetch(url, { headers: { Accept: "application/json" }, cache: "no-store" });
    if (!response.ok) throw new Error();
    const dados = await response.json();
    if (!Array.isArray(dados)) throw new Error();
    return dados;
}

async function carregarOpcoes() {
    abrir.disabled = true;
    buscaEstudante.disabled = true;
    profissional.disabled = true;
    recarregar.hidden = true;
    mensagem.textContent = "Carregando opções…";
    try {
        const profissionais = await consultar("/api/estudos-caso/profissionais");
        profissional.replaceChildren(new Option("Selecione o responsável", ""));
        profissionais.forEach(item => profissional.add(new Option(`${item.nomeProfissional} — ${item.papelCargo}`, item.idProfissional)));
        buscaEstudante.disabled = false;
        profissional.disabled = profissionais.length === 0;
        if (!profissionais.length) {
            informar("Não há profissionais cadastrados. Solicite ao responsável o cadastro do profissional antes de abrir o estudo.");
            recarregar.hidden = false;
            return;
        }
        abrir.disabled = false;
        const matricula = new URLSearchParams(location.search).get("matricula");
        mensagem.textContent = "Preencha os dados da abertura.";
        if (matricula) {
            const resposta = await fetch(`/api/estudantes/${encodeURIComponent(matricula)}`, { cache: "no-store" });
            if (resposta.ok) {
                const item = await resposta.json();
                if (!buscaEstudante.value.trim()) busca.selecionar(item);
            } else informar("A matrícula indicada não foi encontrada. Busque e selecione um estudante.");
        }
    } catch {
        informar("Não foi possível carregar estudantes e profissionais. Verifique a conexão e tente novamente.");
        recarregar.hidden = false;
    }
}

origem.addEventListener("change", () => {
    const outro = origem.value === "Outro";
    document.querySelector("#outra-origem").hidden = !outro;
    outraOrigem.disabled = !outro;
    outraOrigem.required = outro;
    if (!outro) {
        outraOrigem.value = "";
        outraOrigem.setCustomValidity("");
    }
});

form.addEventListener("input", event => {
    const campo = event.target;
    if (campo === buscaEstudante) return;
    campo.removeAttribute("aria-invalid");
    campo.setCustomValidity("");
    const erro = document.getElementById(`erro-${campo.name}`);
    if (erro) erro.textContent = "";
    if (campo.required && !campo.value.trim()) campo.setCustomValidity("Preencha este campo.");
    if (["docsComprobatoriosFlagQuais", "condicaoAssociadaFlagQuais", "medicacaoContinuaFlagQuais"].includes(campo.name)
            && /^sim[\s:–—,;-]*$/i.test(campo.value.trim())) {
        campo.setCustomValidity("Se informar Sim, descreva quais.");
    }
});

form.addEventListener("submit", async event => {
    event.preventDefault();
    if (!estudante.value) buscaEstudante.setCustomValidity("Busque e selecione um estudante.");
    if (abrir.disabled || !form.reportValidity()) return;
    form.querySelectorAll('[id^="erro-"]').forEach(campo => { campo.textContent = ""; });
    form.querySelectorAll('[aria-invalid="true"]').forEach(campo => campo.removeAttribute("aria-invalid"));
    const dados = Object.fromEntries(new FormData(form));
    Object.keys(dados).forEach(chave => { dados[chave] = dados[chave].trim() || null; });
    dados.idProfissionalResponsavel = Number(profissional.value);
    abrir.disabled = true;
    abrir.textContent = "Abrindo…";
    try {
        const response = await fetch("/api/estudos-caso", {
            method: "POST",
            headers: { "Content-Type": "application/json", Accept: "application/json" },
            body: JSON.stringify(dados)
        });
        const corpo = await response.json().catch(() => ({}));
        if (!response.ok) {
            let primeiro;
            Object.entries(corpo.campos || {}).forEach(([nome, texto]) => {
                const campo = nome === "matriculaEstudante" ? buscaEstudante : document.getElementById(nome);
                const erro = document.getElementById(`erro-${nome}`);
                if (campo && erro) {
                    campo.setAttribute("aria-invalid", "true");
                    erro.textContent = texto;
                    primeiro ||= campo;
                }
            });
            informar(corpo.erro || "Não foi possível abrir o estudo. Confira os dados informados.");
            primeiro?.focus();
            return;
        }
        informar(`Estudo de caso ${corpo.idEstudoCaso} aberto com sucesso.`);
        const link = document.querySelector("#ver-historico");
        link.href = `detalhes-estudante.html?matricula=${encodeURIComponent(dados.matriculaEstudante)}`;
        resultado.hidden = false;
        form.hidden = true;
    } catch {
        informar("Falha de conexão. Os dados foram mantidos. Consulte o histórico do estudante antes de reenviar, pois a abertura pode ter sido concluída.");
    } finally {
        abrir.disabled = false;
        abrir.textContent = "Abrir estudo de caso";
    }
});
recarregar.addEventListener("click", carregarOpcoes);
carregarOpcoes();
