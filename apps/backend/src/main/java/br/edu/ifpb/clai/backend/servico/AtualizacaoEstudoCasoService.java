package br.edu.ifpb.clai.backend.servico;

import br.edu.ifpb.clai.backend.dto.AberturaEstudoCaso;
import br.edu.ifpb.clai.backend.dto.AtualizacaoEstudoCaso;
import br.edu.ifpb.clai.backend.dto.EstudoCasoEdicao;
import br.edu.ifpb.clai.backend.dto.ResumoEstudoCaso;
import java.util.List;
import br.edu.ifpb.clai.backend.entidade.CampoEstudoCaso;
import br.edu.ifpb.clai.backend.exception.AtualizacaoEstudoCasoInvalidaException;
import br.edu.ifpb.clai.backend.exception.EstudoCasoBloqueadoException;
import br.edu.ifpb.clai.backend.exception.EstudoCasoNaoEncontradoException;
import br.edu.ifpb.clai.backend.repositorio.AtualizacaoEstudoCasoRepository;
import br.edu.ifpb.clai.backend.repositorio.ProfissionalRepository;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AtualizacaoEstudoCasoService {
    private static final Set<String> PROFISSIONAIS = Set.of(
            "idProfissionalResponsavel", "idPsicopedagogoAssinatura", "idCoordenadorClaiAssinatura");
    private final AtualizacaoEstudoCasoRepository estudos;
    private final ProfissionalRepository profissionais;
    private final Validator validator;

    public AtualizacaoEstudoCasoService(AtualizacaoEstudoCasoRepository estudos,
            ProfissionalRepository profissionais, Validator validator) {
        this.estudos = estudos;
        this.profissionais = profissionais;
        this.validator = validator;
    }

    @Transactional(readOnly = true)
    public List<ResumoEstudoCaso> listar(String matricula) {
        return estudos.listar(normalizar(matricula));
    }

    @Transactional(readOnly = true)
    public EstudoCasoEdicao consultar(Integer id) {
        return encontrar(id, false);
    }

    @Transactional
    public EstudoCasoEdicao atualizar(Integer id, AtualizacaoEstudoCaso dados) {
        var atual = encontrar(id, true);
        if (!atual.editavel()) throw new EstudoCasoBloqueadoException(atual.motivoBloqueio());
        Map<String, String> erros = new LinkedHashMap<>();
        if (dados.campos().isEmpty() && dados.profissionais().isEmpty()) {
            erros.put("atualizacao", "Informe ao menos um campo para atualizar.");
        }
        var campos = new LinkedHashMap<>(atual.campos());
        dados.campos().forEach((chave, valor) -> {
            var campo = CampoEstudoCaso.porChave(chave);
            if (campo.isEmpty()) {
                erros.put(chave, "Campo não editável nesta tarefa.");
                return;
            }
            String normalizado = normalizar(valor);
            if (normalizado != null && campo.get().limite() > 0 && normalizado.length() > campo.get().limite()) {
                erros.put(chave, "Use no máximo " + campo.get().limite() + " caracteres.");
            }
            campos.put(chave, normalizado);
        });
        var referencias = new LinkedHashMap<>(atual.profissionais());
        dados.profissionais().forEach((chave, valor) -> {
            if (!PROFISSIONAIS.contains(chave)) erros.put(chave, "Referência não editável nesta tarefa.");
            else referencias.put(chave, valor);
        });
        if (!erros.isEmpty()) throw new AtualizacaoEstudoCasoInvalidaException(erros);

        // Reutiliza as regras já entregues para os campos obrigatórios do mesmo schema.
        var obrigatorios = new AberturaEstudoCaso(atual.matriculaEstudante(),
                referencias.get("idProfissionalResponsavel"), campos.get("periodoLetivo"),
                campos.get("datasAtendimentoAnamnese"), campos.get("origemEncaminhamento"),
                campos.get("origemEncaminhamentoOutro"), campos.get("motivoPrincipal"),
                campos.get("demandaEstudante"), campos.get("demandaFamiliaResponsavel"),
                campos.get("demandaEquipeEscolar"), campos.get("deficienciaNecessidadeInformada"),
                campos.get("docsComprobatoriosFlagQuais"), campos.get("condicaoAssociadaFlagQuais"),
                campos.get("medicacaoContinuaFlagQuais"));
        validator.validate(obrigatorios).forEach(erro -> {
            String campo = erro.getPropertyPath().toString();
            if ("origemOutroInformada".equals(campo)) campo = "origemEncaminhamentoOutro";
            erros.putIfAbsent(campo, erro.getMessage());
        });
        referencias.forEach((chave, valor) -> {
            if (valor != null && (valor <= 0 || profissionais.findById(valor).isEmpty())) {
                erros.putIfAbsent(chave, "Selecione um profissional existente e válido.");
            }
        });
        if (!erros.isEmpty()) throw new AtualizacaoEstudoCasoInvalidaException(erros);
        campos.put("origemEncaminhamentoOutro", obrigatorios.origemEncaminhamentoOutro());

        if (estudos.atualizar(id, campos, referencias) != 1) {
            throw new EstudoCasoBloqueadoException("O estudo não pode mais ser editado. Recarregue os dados.");
        }
        return encontrar(id, false);
    }

    private EstudoCasoEdicao encontrar(Integer id, boolean bloquear) {
        return estudos.buscar(id, bloquear).orElseThrow(EstudoCasoNaoEncontradoException::new);
    }

    private static String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.strip();
    }
}
