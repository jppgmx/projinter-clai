package br.edu.ifpb.clai.backend.servico;

import br.edu.ifpb.clai.backend.dto.DocumentoResumo;
import br.edu.ifpb.clai.backend.dto.DocumentoConteudo;
import br.edu.ifpb.clai.backend.entidade.ArquivoMedico;
import br.edu.ifpb.clai.backend.exception.*;
import br.edu.ifpb.clai.backend.repositorio.DocumentoMedicoRepository;
import br.edu.ifpb.clai.backend.repositorio.SituacaoEstudoCasoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DocumentoMedicoService {
    private final DocumentoMedicoRepository documentos;
    private final SituacaoEstudoCasoRepository estudos;
    public DocumentoMedicoService(DocumentoMedicoRepository documentos, SituacaoEstudoCasoRepository estudos) {
        this.documentos = documentos; this.estudos = estudos;
    }
    @Transactional(readOnly = true)
    public List<DocumentoResumo> listar(Integer id) {
        estudos.buscar(id, false).orElseThrow(EstudoCasoNaoEncontradoException::new);
        return documentos.listar(id);
    }
    @Transactional
    public List<DocumentoResumo> enviar(Integer id, List<ArquivoMedico> arquivos) {
        if (arquivos.isEmpty() || arquivos.size() > 10) throw new DocumentoInvalidoException("Selecione de 1 a 10 arquivos.");
        var estudo = estudos.buscar(id, true).orElseThrow(EstudoCasoNaoEncontradoException::new);
        if (estudo.arquivado() || estudo.trancado() || estudo.dataFechamento() != null) {
            throw new EstudoCasoBloqueadoException("Estudo arquivado, trancado ou finalizado não aceita novos documentos.");
        }
        for (var arquivo : arquivos) documentos.salvar(id, arquivo);
        return documentos.listar(id);
    }
    @Transactional(readOnly = true)
    public DocumentoConteudo obter(Integer id, Integer documento) {
        estudos.buscar(id, false).orElseThrow(EstudoCasoNaoEncontradoException::new);
        return documentos.buscar(id, documento).orElseThrow(DocumentoNaoEncontradoException::new);
    }
}
