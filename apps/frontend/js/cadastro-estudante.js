"use strict";
const form = document.querySelector("#cadastro");
const curso = document.querySelector("#curso");
const salvar = document.querySelector("#salvar");
const mensagem = document.querySelector("#mensagem");
const recarregar = document.querySelector("#recarregar");

function informar(texto, tipo) {
    mensagem.textContent = texto;
    mensagem.dataset.tipo = tipo;
    mensagem.hidden = false;
    mensagem.focus();
}

async function carregarCursos() {
    curso.disabled = true;
    salvar.disabled = true;
    recarregar.hidden = true;
    try {
        const response = await fetch("/api/cursos", { headers: { Accept: "application/json" } });
        if (!response.ok) throw new Error();
        const cursos = await response.json();
        if (!Array.isArray(cursos)) throw new Error();
        curso.replaceChildren(new Option("Selecione um curso", ""));
        cursos.forEach(item => curso.add(new Option(item.nomeCurso, item.idCurso)));
        if (!cursos.length) {
            informar("Não há cursos disponíveis. Solicite o cadastro de um curso para continuar.", "erro");
            recarregar.hidden = false;
            return;
        }
        curso.disabled = false;
        salvar.disabled = false;
        mensagem.hidden = true;
    } catch {
        informar("Não foi possível carregar os cursos. Verifique a conexão e tente novamente.", "erro");
        recarregar.hidden = false;
    }
}

form.addEventListener("input", event => {
    const campo = event.target;
    campo.removeAttribute("aria-invalid");
    campo.setCustomValidity("");
    const erro = document.getElementById(`erro-${campo.name}`);
    if (erro) erro.textContent = "";
    if (campo.required && !campo.value.trim()) campo.setCustomValidity("Preencha este campo.");
});

form.addEventListener("submit", async event => {
    event.preventDefault();
    if (salvar.disabled || !form.reportValidity()) return;
    form.querySelectorAll('[id^="erro-"]').forEach(campo => { campo.textContent = ""; });
    form.querySelectorAll('[aria-invalid="true"]').forEach(campo => campo.removeAttribute("aria-invalid"));
    const dados = Object.fromEntries(new FormData(form));
    Object.keys(dados).forEach(chave => { dados[chave] = dados[chave].trim() || null; });
    dados.curso = { idCurso: Number(curso.value) };
    salvar.disabled = true;
    salvar.textContent = "Registrando…";
    mensagem.hidden = true;
    try {
        const response = await fetch("/api/estudantes", {
            method: "POST",
            headers: { "Content-Type": "application/json", Accept: "application/json" },
            body: JSON.stringify(dados)
        });
        const resultado = await response.json().catch(() => ({}));
        if (!response.ok) {
            let primeiroCampo;
            Object.entries(resultado.campos || {}).forEach(([nome, texto]) => {
                const id = nome.startsWith("curso") ? "curso" : nome;
                const campo = document.getElementById(id);
                const erro = document.getElementById(`erro-${id}`);
                if (campo && erro) {
                    campo.setAttribute("aria-invalid", "true");
                    erro.textContent = texto;
                    primeiroCampo ||= campo;
                }
            });
            informar(resultado.erro || "Não foi possível registrar o estudante. Tente novamente.", "erro");
            primeiroCampo?.focus();
            return;
        }
        form.reset();
        informar("Estudante registrado com sucesso.", "sucesso");
    } catch {
        informar("Falha de conexão. Os dados foram mantidos. Tente novamente; se o cadastro já tiver sido salvo, a matrícula será indicada como duplicada.", "erro");
    } finally {
        salvar.disabled = false;
        salvar.textContent = "Registrar estudante";
    }
});
recarregar.addEventListener("click", carregarCursos);
carregarCursos();
