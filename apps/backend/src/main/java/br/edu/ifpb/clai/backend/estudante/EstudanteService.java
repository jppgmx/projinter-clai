package br.edu.ifpb.clai.backend.estudante;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Matrícula já cadastrada.");
        }
        Curso curso = cursos.findById(dados.curso().idCurso())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Curso informado não encontrado."));
        Estudante estudante = new Estudante(dados, curso);
        estudantes.cadastrar(estudante);
        return estudante;
    }
}
