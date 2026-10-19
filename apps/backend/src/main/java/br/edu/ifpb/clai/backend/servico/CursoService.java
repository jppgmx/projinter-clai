package br.edu.ifpb.clai.backend.servico;

import br.edu.ifpb.clai.backend.dto.CursoEstudante;
import br.edu.ifpb.clai.backend.repositorio.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CursoService {
    private final CursoRepository cursos;

    public CursoService(CursoRepository cursos) {
        this.cursos = cursos;
    }

    @Transactional(readOnly = true)
    public List<CursoEstudante> listar() {
        return cursos.findAllByOrderByNomeCursoAsc().stream().map(CursoEstudante::de).toList();
    }
}
