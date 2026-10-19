package br.edu.ifpb.clai.backend.repositorio;

import br.edu.ifpb.clai.backend.entidade.Curso;
import org.springframework.data.repository.Repository;
import java.util.List;
import java.util.Optional;

public interface CursoRepository extends Repository<Curso, Integer> {
    Optional<Curso> findById(Integer id);
    List<Curso> findAllByOrderByNomeCursoAsc();
}
