package br.edu.ifpb.clai.backend.servico;

import br.edu.ifpb.clai.backend.entidade.Curso;
import br.edu.ifpb.clai.backend.entidade.Estudante;
import br.edu.ifpb.clai.backend.repositorio.CursoRepository;
import br.edu.ifpb.clai.backend.repositorio.EstudanteRepository;
import br.edu.ifpb.clai.backend.dto.RegistroEstudante;
import br.edu.ifpb.clai.backend.exception.MatriculaDuplicadaException;
import br.edu.ifpb.clai.backend.exception.CursoInvalidoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstudanteService {
    private final EstudanteRepository estudantes;
    private final CursoRepository cursos;

    public EstudanteService(EstudanteRepository estudantes, CursoRepository cursos) {
        this.estudantes = estudantes;
        this.cursos = cursos;
    }

    @Transactional
    public Estudante registrar(RegistroEstudante dados) {
        if (estudantes.existe(dados.matricula())) {
            throw new MatriculaDuplicadaException();
        }
        Curso curso = cursos.findById(dados.curso().idCurso())
                .orElseThrow(CursoInvalidoException::new);
        Estudante estudante = new Estudante(dados.matricula(), dados.turma(), dados.nomeEstudante(),
                dados.periodoIngresso(), dados.dataNascimentoIdade(), dados.telefoneEstudante(),
                dados.emailEstudante(), dados.nomeResponsavel(), dados.grauParentesco(),
                dados.telefoneResponsavel(), dados.emailResponsavel(), curso);
        estudantes.cadastrar(estudante);
        return estudante;
    }
}
