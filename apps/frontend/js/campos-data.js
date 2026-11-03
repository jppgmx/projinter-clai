import { dataISO } from "./campos-estudante.js";

// Os campos existentes continuam sendo enviados como texto, sem mudar o banco.
export function configurarDatas(id, { multiplo = false, complemento = "Observações anteriores" } = {}) {
    const original = document.getElementById(id);
    const obrigatorio = original.required;
    original.hidden = true;
    original.required = false;
    const grupo = document.createElement("span");
    grupo.className = "campos-data";
    original.after(grupo);
    function preencher(valor = "") {
        original.value = valor;
        grupo.replaceChildren();
        const encontrados = String(valor).match(/\b(?:\d{4}-\d{2}-\d{2}|\d{2}\/\d{2}\/\d{4})\b/g) ?? [];
        const datas = encontrados.filter(v => dataISO(v));
        const datasUsadas = multiplo ? datas : datas.slice(0, 1);
        let observacao = String(valor);
        datasUsadas.forEach(data => { observacao = observacao.replace(data, ""); });
        observacao = observacao.replace(/^[\s;,–—-]+|[\s;,–—-]+$/g, "");
        const lista = document.createElement("span");
        lista.className = "lista-datas";
        const nota = document.createElement("textarea");
        nota.rows = 2;
        nota.value = observacao;
        nota.setAttribute("aria-label", complemento);
        // Preserve informações antigas que não eram exclusivamente datas.
        nota.hidden = !observacao && multiplo;
        function atualizar() {
            const inputs = Array.from(lista.querySelectorAll('input[type="date"]'));
            const valores = inputs.map(campo => campo.value).filter(Boolean).map(data => data.split("-").reverse().join("/"));
            original.value = [...valores, nota.value.trim()].filter(Boolean).join("; ");
            inputs.forEach(campo => { campo.required = false; campo.removeAttribute("id"); });
            inputs[0].required = obrigatorio;
            inputs[0].id = `${id}-data`;
        }
        function adicionar(valorData = "") {
            const linha = document.createElement("span");
            const campo = document.createElement("input");
            campo.type = "date";
            campo.value = dataISO(valorData);
            campo.setAttribute("aria-label", multiplo ? "Data do atendimento / anamnese" : "Data do documento");
            campo.setAttribute("aria-describedby", `erro-${id}`);
            campo.addEventListener("input", atualizar);
            linha.append(campo);
            if (multiplo) {
                const remover = document.createElement("button");
                remover.type = "button";
                remover.textContent = "Remover data";
                remover.addEventListener("click", () => {
                    linha.remove();
                    if (!lista.children.length) adicionar();
                    atualizar();
                    lista.querySelector("input").focus();
                });
                linha.append(remover);
            }
            lista.append(linha);
            return campo;
        }
        grupo.append(lista);
        (datasUsadas.length ? datasUsadas : [""]).forEach(data => adicionar(data));
        const primeiro = lista.querySelector("input");
        primeiro.id = `${id}-data`;
        primeiro.required = obrigatorio && (!valor || !!datasUsadas.length);
        const label = document.querySelector(`label[for="${id}"], label[for="${id}-data"]`);
        if (label) label.htmlFor = primeiro.id;
        if (multiplo) {
            const adicionarData = document.createElement("button");
            adicionarData.type = "button";
            adicionarData.textContent = "Adicionar data";
            adicionarData.addEventListener("click", () => { adicionar().focus(); });
            grupo.append(adicionarData);
        }
        if (!multiplo || observacao) {
            const texto = document.createElement("small");
            texto.textContent = complemento;
            grupo.append(texto, nota);
        }
        nota.addEventListener("input", atualizar);
    }
    preencher(original.value);
    return { preencher };
}
