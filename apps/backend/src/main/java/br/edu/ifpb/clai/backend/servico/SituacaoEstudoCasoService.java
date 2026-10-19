package br.edu.ifpb.clai.backend.servico;

import br.edu.ifpb.clai.backend.dto.SituacaoEstudoCaso;
import br.edu.ifpb.clai.backend.exception.EstudoCasoBloqueadoException;
import br.edu.ifpb.clai.backend.exception.EstudoCasoNaoEncontradoException;
import br.edu.ifpb.clai.backend.repositorio.SituacaoEstudoCasoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SituacaoEstudoCasoService {
    private final SituacaoEstudoCasoRepository estudos;

    public SituacaoEstudoCasoService(SituacaoEstudoCasoRepository estudos) {
        this.estudos = estudos;
    }

    @Transactional(readOnly = true)
    public SituacaoEstudoCaso consultar(Integer id) {
        return encontrar(id, false);
    }

    @Transactional
    public SituacaoEstudoCaso arquivar(Integer id, String motivo) {
        String texto = obrigatorio(motivo, "Informe o motivo do arquivamento.");
        var estudo = encontrar(id, true);
        if (estudo.arquivado()) throw new EstudoCasoBloqueadoException("Estudo de caso já está arquivado.");
        if (estudos.arquivar(id, texto) != 1) throw conflito();
        return encontrar(id, false);
    }

    @Transactional
    public SituacaoEstudoCaso desarquivar(Integer id) {
        var estudo = encontrar(id, true);
        if (!estudo.arquivado()) throw new EstudoCasoBloqueadoException("Estudo de caso não está arquivado.");
        if (estudos.desarquivar(id) != 1) throw conflito();
        return encontrar(id, false);
    }

    @Transactional
    public SituacaoEstudoCaso trancar(Integer id, String justificativa) {
        String texto = obrigatorio(justificativa, "Informe a justificativa do trancamento.");
        var estudo = encontrar(id, true);
        if (estudo.arquivado()) throw new EstudoCasoBloqueadoException("Estudo de caso arquivado não pode ser trancado.");
        if (estudo.trancado()) throw new EstudoCasoBloqueadoException("Estudo de caso já está trancado.");
        if (estudo.dataFechamento() != null) throw new EstudoCasoBloqueadoException("Estudo de caso finalizado não pode ser trancado.");
        if (estudos.trancar(id, texto) != 1) throw conflito();
        return encontrar(id, false);
    }

    private SituacaoEstudoCaso encontrar(Integer id, boolean bloquear) {
        return estudos.buscar(id, bloquear).orElseThrow(EstudoCasoNaoEncontradoException::new);
    }

    private static String obrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException(mensagem);
        return valor.strip();
    }

    private static EstudoCasoBloqueadoException conflito() {
        return new EstudoCasoBloqueadoException("A situação do estudo mudou. Recarregue os dados antes de tentar novamente.");
    }
}
