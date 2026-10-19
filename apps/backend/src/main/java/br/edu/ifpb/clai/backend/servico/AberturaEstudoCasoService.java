package br.edu.ifpb.clai.backend.servico;

import br.edu.ifpb.clai.backend.dto.AberturaEstudoCaso;
import br.edu.ifpb.clai.backend.dto.EstudoCasoAberto;
import br.edu.ifpb.clai.backend.dto.ProfissionalResponsavel;
import br.edu.ifpb.clai.backend.entidade.EstudoCaso;
import br.edu.ifpb.clai.backend.exception.EstudanteNaoEncontradoException;
import br.edu.ifpb.clai.backend.exception.ProfissionalNaoEncontradoException;
import br.edu.ifpb.clai.backend.repositorio.EstudanteRepository;
import br.edu.ifpb.clai.backend.repositorio.EstudoCasoRepository;
import br.edu.ifpb.clai.backend.repositorio.ProfissionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AberturaEstudoCasoService {
    private final EstudanteRepository estudantes;
    private final ProfissionalRepository profissionais;
    private final EstudoCasoRepository estudos;

    public AberturaEstudoCasoService(EstudanteRepository estudantes,
            ProfissionalRepository profissionais, EstudoCasoRepository estudos) {
        this.estudantes = estudantes;
        this.profissionais = profissionais;
        this.estudos = estudos;
    }

    @Transactional(readOnly = true)
    public List<ProfissionalResponsavel> listarProfissionais() {
        return profissionais.findAllByOrderByNomeProfissionalAsc().stream()
                .map(ProfissionalResponsavel::de).toList();
    }

    @Transactional
    public EstudoCasoAberto abrir(AberturaEstudoCaso dados) {
        var estudante = estudantes.buscarComCurso(dados.matriculaEstudante())
                .orElseThrow(EstudanteNaoEncontradoException::new);
        var profissional = profissionais.findById(dados.idProfissionalResponsavel())
                .orElseThrow(ProfissionalNaoEncontradoException::new);
        var estudo = new EstudoCaso(estudante, profissional,
                dados.periodoLetivo(), dados.datasAtendimentoAnamnese(), dados.origemEncaminhamento(),
                dados.origemEncaminhamentoOutro(), dados.motivoPrincipal(), dados.demandaEstudante(),
                dados.demandaFamiliaResponsavel(), dados.demandaEquipeEscolar(),
                dados.deficienciaNecessidadeInformada(), dados.docsComprobatoriosFlagQuais(),
                dados.condicaoAssociadaFlagQuais(), dados.medicacaoContinuaFlagQuais());
        estudos.cadastrar(estudo);
        return EstudoCasoAberto.de(estudo);
    }
}
