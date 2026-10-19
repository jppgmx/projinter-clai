"use strict";
const consulta = document.getElementById("consulta-documentos");
const numero = document.getElementById("id-estudo");
const secao = document.getElementById("documentos");
const form = document.getElementById("envio-documentos");
const arquivos = document.getElementById("arquivos");
const mensagem = document.getElementById("mensagem");
let estudo;
let ocupado = false;
let recarga = false;
function informar(texto) { mensagem.textContent = texto; mensagem.focus(); }
function exibir(lista) {
    const tbody = document.getElementById("lista-documentos");
    tbody.replaceChildren();
    lista.forEach(documento => {
        const tr = document.createElement("tr");
        [documento.nomeArquivo, `${(documento.tamanhoBytes / 1024).toFixed(1)} KB`, documento.dataUpload?.replace("T", " ")].forEach(valor => {
            const td = document.createElement("td"); td.textContent = valor ?? "Não informado"; tr.append(td);
        });
        const td = document.createElement("td");
        ["visualizar", "download"].forEach(acao => {
            const link = document.createElement("a");
            link.href = `/api/estudos-caso/${estudo.idEstudoCaso}/documentos/${documento.idDocumento}/${acao}`;
            link.textContent = acao === "visualizar" ? "Visualizar" : "Baixar";
            link.target = "_blank"; link.rel = "noopener";
            td.append(link, document.createTextNode(" "));
        });
        tr.append(td); tbody.append(tr);
    });
    document.getElementById("tabela-documentos").hidden = lista.length === 0;
}
async function obter(url) {
    const response = await fetch(url, { cache: "no-store" });
    const body = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(body.erro || "Não foi possível consultar os documentos.");
    return body;
}
async function carregar() {
    if (ocupado || !consulta.reportValidity()) return;
    ocupado = true; secao.hidden = true; estudo = null;
    try {
        const id = Number(numero.value);
        const [atual, lista] = await Promise.all([obter(`/api/estudos-caso/${id}`), obter(`/api/estudos-caso/${id}/documentos`)]);
        estudo = atual;
        if (!Array.isArray(lista)) throw new Error("Resposta inválida ao consultar documentos.");
        exibir(lista);
        secao.hidden = false; recarga = false;
        document.getElementById("titulo-documentos").textContent = `Estudo ${id} — ${atual.nomeEstudante}`;
        document.getElementById("campos-documentos").disabled = !atual.editavel;
        document.getElementById("bloqueio-documentos").textContent = atual.editavel ? "" : atual.motivoBloqueio;
        history.replaceState(null, "", `${location.pathname}?id=${id}`);
        informar(lista.length ? `${lista.length} documento(s) vinculado(s).` : "Nenhum documento vinculado a este estudo.");
    } catch (erro) { informar(erro.message); }
    finally { ocupado = false; }
}
consulta.addEventListener("submit", event => { event.preventDefault(); carregar(); });
form.addEventListener("submit", async event => {
    event.preventDefault();
    if (ocupado || recarga || !estudo?.editavel || !form.reportValidity()) return;
    const lista = [...arquivos.files];
    if (!lista.length || lista.length > 10 || lista.some(f => f.size > 10 * 1024 * 1024 || !f.size)) {
        informar("Selecione de 1 a 10 arquivos não vazios, com até 10 MB cada."); return;
    }
    const body = new FormData(); lista.forEach(arquivo => body.append("arquivos", arquivo));
    ocupado = true;
    document.getElementById("campos-documentos").disabled = true;
    consulta.querySelector("button").disabled = true;
    numero.disabled = true;
    try {
        const response = await fetch(`/api/estudos-caso/${estudo.idEstudoCaso}/documentos`, { method: "POST", body });
        const resultado = await response.json().catch(() => ({}));
        if (!response.ok) {
            recarga = response.status !== 400 && response.status !== 413;
            throw new Error((resultado.erro || "Não foi possível enviar os documentos.") + (recarga ? " Carregue os documentos novamente antes de reenviar." : ""));
        }
        exibir(resultado); form.reset(); informar("Documentos anexados com sucesso.");
    } catch (erro) {
        if (erro instanceof TypeError) { recarga = true; informar("Não foi possível confirmar o envio. Carregue os documentos novamente para conferir antes de reenviar."); }
        else informar(erro.message);
    } finally {
        ocupado = false;
        document.getElementById("campos-documentos").disabled = recarga || !estudo?.editavel;
        consulta.querySelector("button").disabled = false; numero.disabled = false;
    }
});
const id = new URLSearchParams(location.search).get("id");
if (id) { numero.value = id; carregar(); }
