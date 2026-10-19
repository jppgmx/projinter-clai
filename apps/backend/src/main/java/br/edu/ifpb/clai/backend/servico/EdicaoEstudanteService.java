package br.edu.ifpb.clai.backend.servico;

import br.edu.ifpb.clai.backend.dto.RegistroEstudante;
import br.edu.ifpb.clai.backend.dto.DetalhesEstudante;
import br.edu.ifpb.clai.backend.exception.CursoInvalidoException;
import br.edu.ifpb.clai.backend.exception.EstudanteNaoEncontradoException;
import br.edu.ifpb.clai.backend.repositorio.CursoRepository;
import br.edu.ifpb.clai.backend.repositorio.EstudanteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EdicaoEstudanteService {
    private final EstudanteRepository estudantes;
    private final CursoRepository cursos;
    private final ConsultaEstudanteService consulta;
    public EdicaoEstudanteService(EstudanteRepository estudantes, CursoRepository cursos, ConsultaEstudanteService consulta) {
        this.estudantes = estudantes; this.cursos = cursos; this.consulta = consulta;
    }

    @Transactional
    public DetalhesEstudante atualizar(String matricula, RegistroEstudante dados) {
        if (!matricula.equals(dados.matricula())) throw new IllegalArgumentException("A matrícula não pode ser alterada.");
        var estudante = estudantes.buscarComCurso(matricula).orElseThrow(EstudanteNaoEncontradoException::new);
        var curso = cursos.findById(dados.curso().idCurso()).orElseThrow(CursoInvalidoException::new);
        estudante.atualizar(dados.turma(), dados.nomeEstudante(), dados.periodoIngresso(), dados.dataNascimentoIdade(),
                dados.telefoneEstudante(), dados.emailEstudante(), dados.nomeResponsavel(), dados.grauParentesco(),
                dados.telefoneResponsavel(), dados.emailResponsavel(), curso);
        return consulta.detalhar(matricula);
    }
}
