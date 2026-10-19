"use strict";
const form = document.getElementById("cadastro");
const curso = document.getElementById("curso");
const salvar = document.getElementById("salvar");
const mensagem = document.getElementById("mensagem");
const recarregar = document.getElementById("recarregar");
const matricula = new URLSearchParams(location.search).get("matricula");
let enviando = false;
let recarga = false;

function informar(texto) { mensagem.textContent = texto; mensagem.hidden = false; mensagem.focus(); }
async function carregar() {
    salvar.disabled = true;
    recarregar.hidden = true;
    if (!matricula) { informar("Selecione o estudante na consulta para editar."); return; }
    try {
        const respostas = await Promise.all([fetch("/api/cursos"), fetch(`/api/estudantes/${encodeURIComponent(matricula)}`, { cache: "no-store" })]);
        if (respostas.some(r => !r.ok)) throw new Error("Não foi possível carregar o estudante e os cursos.");
        const [cursos, estudante] = await Promise.all(respostas.map(r => r.json()));
        curso.replaceChildren(new Option("Selecione um curso", ""));
        cursos.forEach(item => curso.add(new Option(item.nomeCurso, item.idCurso)));
        form.querySelectorAll("input[name]").forEach(campo => { campo.value = estudante[campo.name] ?? ""; });
        curso.value = estudante.curso.idCurso;
        curso.disabled = false;
        document.getElementById("matricula").readOnly = true;
        salvar.disabled = false;
        recarga = false;
        informar("Dados carregados. A matrícula é preservada; altere os demais campos e salve.");
    } catch (erro) { informar(erro.message); recarregar.hidden = false; }
}
form.addEventListener("input", event => {
    event.target.setCustomValidity("");
    event.target.removeAttribute("aria-invalid");
    const erro = document.getElementById(`erro-${event.target.id}`);
    if (erro) erro.textContent = "";
    if (event.target.required && !event.target.value.trim()) event.target.setCustomValidity("Preencha este campo.");
});
form.addEventListener("submit", async event => {
    event.preventDefault();
    if (enviando || recarga || salvar.disabled || !form.reportValidity()) return;
    const dados = Object.fromEntries(new FormData(form));
    Object.keys(dados).forEach(chave => { dados[chave] = dados[chave].trim() || null; });
    dados.matricula = matricula;
    dados.curso = { idCurso: Number(curso.value) };
    form.querySelectorAll('[id^="erro-"]').forEach(campo => { campo.textContent = ""; });
    enviando = true;
    salvar.disabled = true;
    recarregar.disabled = true;
    try {
        const response = await fetch(`/api/estudantes/${encodeURIComponent(matricula)}`, {
            method: "PUT", headers: { "Content-Type": "application/json" }, body: JSON.stringify(dados)
        });
        const resultado = await response.json().catch(() => ({}));
        if (!response.ok) {
            Object.entries(resultado.campos || {}).forEach(([nome, texto]) => {
                const id = nome.startsWith("curso") ? "curso" : nome;
                const campo = document.getElementById(id), erro = document.getElementById(`erro-${id}`);
                if (campo && erro) { campo.setAttribute("aria-invalid", "true"); erro.textContent = texto; }
            });
            if (response.status >= 500 || response.status === 404 || response.status === 409) recarga = true;
            informar(resultado.erro || "Não foi possível atualizar o cadastro.");
            return;
        }
        informar("Cadastro atualizado com sucesso. A matrícula e o histórico foram preservados.");
    } catch {
        recarga = true;
        informar("Não foi possível confirmar o salvamento. Recarregue o cadastro antes de reenviar.");
    } finally {
        enviando = false;
        salvar.disabled = recarga;
        recarregar.disabled = false;
        recarregar.hidden = !recarga;
    }
});
recarregar.addEventListener("click", carregar);
carregar();
