package br.edu.ifpb.clai.backend.servico;

import br.edu.ifpb.clai.backend.dto.DetalhesEstudante;
import br.edu.ifpb.clai.backend.dto.ResumoEstudante;
import br.edu.ifpb.clai.backend.repositorio.EstudanteRepository;
import br.edu.ifpb.clai.backend.repositorio.EstudoCasoRepository;
import br.edu.ifpb.clai.backend.exception.EstudanteNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class ConsultaEstudanteService {
    private final EstudanteRepository estudantes;
    private final EstudoCasoRepository estudos;

    public ConsultaEstudanteService(EstudanteRepository estudantes, EstudoCasoRepository estudos) {
        this.estudantes = estudantes;
        this.estudos = estudos;
    }

    public List<ResumoEstudante> consultar(String busca) {
        String termo = busca == null ? "" : busca.strip().toLowerCase(Locale.ROOT);
        return estudantes.consultar(termo).stream().map(ResumoEstudante::de).toList();
    }

    public DetalhesEstudante detalhar(String matricula) {
        var estudante = estudantes.buscarComCurso(matricula)
                .orElseThrow(EstudanteNaoEncontradoException::new);
        return DetalhesEstudante.de(estudante, estudos.consultarHistorico(matricula));
    }
}
