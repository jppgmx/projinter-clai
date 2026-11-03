export function configurarBuscaEstudante(campo, matricula) {
    const lista = document.getElementById("sugestoes-estudantes");
    const status = document.getElementById("status-busca");
    let timer, requisicao, ativo = -1;
    function fechar() {
        lista.hidden = true;
        campo.setAttribute("aria-expanded", "false");
        campo.removeAttribute("aria-activedescendant");
        ativo = -1;
    }
    function cancelar() { clearTimeout(timer); requisicao?.abort(); }
    function selecionar(item) {
        cancelar();
        matricula.value = item.matricula;
        campo.value = item.matricula;
        campo.setCustomValidity("");
        campo.removeAttribute("aria-invalid");
        document.getElementById("erro-matriculaEstudante").textContent = "";
        status.textContent = "Estudante selecionado.";
        fechar();
    }
    campo.addEventListener("input", () => {
        cancelar(); fechar(); lista.replaceChildren();
        matricula.value = "";
        campo.setCustomValidity("Selecione um estudante entre os resultados da busca.");
        const termo = campo.value.trim();
        status.textContent = termo.length < 3 ? "Digite pelo menos 3 caracteres." : "Buscando estudantes…";
        if (termo.length < 3) return;
        timer = setTimeout(async () => {
            const controller = new AbortController();
            requisicao = controller;
            try {
                const resposta = await fetch(`/api/estudantes?busca=${encodeURIComponent(termo)}`, {
                    signal: controller.signal, cache: "no-store", headers: { Accept: "application/json" }
                });
                if (!resposta.ok) throw new Error();
                const itens = await resposta.json();
                if (!Array.isArray(itens)) throw new Error();
                if (controller.signal.aborted || campo.value.trim() !== termo) return;
                const sugestoes = itens.slice(0, 5);
                lista.replaceChildren();
                sugestoes.forEach((item, indice) => {
                    const li = document.createElement("li");
                    li.id = `sugestao-estudante-${indice}`;
                    li.setAttribute("role", "option");
                    li.setAttribute("aria-selected", "false");
                    const botao = document.createElement("button");
                    botao.type = "button"; botao.tabIndex = -1;
                    botao.textContent = `${item.nomeEstudante} (${item.matricula})`;
                    botao.addEventListener("pointerdown", e => e.preventDefault());
                    botao.addEventListener("click", () => { selecionar(item); campo.focus(); });
                    li.append(botao); lista.append(li);
                });
                lista.hidden = !sugestoes.length;
                campo.setAttribute("aria-expanded", String(!!sugestoes.length));
                status.textContent = sugestoes.length ? `${sugestoes.length} resultado(s). Use as setas e Enter para selecionar.` : "Nenhum estudante encontrado.";
            } catch (erro) {
                if (erro.name !== "AbortError" && !controller.signal.aborted && campo.value.trim() === termo) {
                    fechar(); status.textContent = "Não foi possível buscar estudantes. Digite novamente para tentar.";
                }
            }
        }, 250);
    });
    campo.addEventListener("keydown", e => {
        if (e.key === "Escape") { cancelar(); fechar(); return; }
        if (lista.hidden) return;
        const opcoes = Array.from(lista.children);
        if (["ArrowDown", "ArrowUp"].includes(e.key)) {
            e.preventDefault();
            ativo = (ativo + (e.key === "ArrowDown" ? 1 : -1) + opcoes.length) % opcoes.length;
            opcoes.forEach((opcao, i) => opcao.setAttribute("aria-selected", String(i === ativo)));
            campo.setAttribute("aria-activedescendant", opcoes[ativo].id);
        } else if (e.key === "Enter" && ativo >= 0) {
            e.preventDefault(); opcoes[ativo].querySelector("button").click();
        }
    });
    campo.addEventListener("blur", () => { cancelar(); fechar(); });
    return { selecionar };
}
