export function dataISO(valor) {
    const texto = String(valor ?? "");
    const iso = texto.match(/\b(\d{4})-(\d{2})-(\d{2})\b/);
    const br = texto.match(/\b(\d{2})\/(\d{2})\/(\d{4})\b/);
    const resultado = iso ? iso[0] : br ? `${br[3]}-${br[2]}-${br[1]}` : "";
    if (!resultado) return "";
    const date = new Date(`${resultado}T12:00:00Z`);
    return !Number.isNaN(date.getTime()) && date.toISOString().slice(0, 10) === resultado ? resultado : "";
}

export function preencherEstudante(form, estudante) {
    form.querySelectorAll("input[name], select[name]").forEach(campo => {
        if (campo.name === "curso") return;
        const valor = estudante[campo.name] ?? "";
        if (campo.tagName === "SELECT" && valor && !Array.from(campo.options).some(o => o.value === valor)) {
            campo.add(new Option(valor, valor));
        }
        campo.value = campo.type === "date" ? dataISO(valor) : valor;
    });
    const nascimento = form.elements.dataNascimentoIdade;
    nascimento.dataset.original = estudante.dataNascimentoIdade ?? "";
    nascimento.dataset.inicial = nascimento.value;
    // Datas antigas que só informam idade não podem ser convertidas com segurança.
    nascimento.required = !nascimento.dataset.original || !!nascimento.value;
    let aviso = document.getElementById("nascimento-anterior");
    if (!aviso) { aviso = document.createElement("small"); aviso.id = "nascimento-anterior"; nascimento.after(aviso); }
    aviso.textContent = nascimento.dataset.original && !nascimento.value
        ? `Valor anterior preservado: ${nascimento.dataset.original}. Informe uma data somente para substituí-lo.` : "";
}

export function preservarNascimento(form, dados) {
    const campo = form.elements.dataNascimentoIdade;
    if (campo.dataset.original && campo.value === campo.dataset.inicial) dados.dataNascimentoIdade = campo.dataset.original;
}
