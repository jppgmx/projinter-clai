package br.edu.ifpb.clai.backend.estudante;

import org.springframework.data.repository.Repository;
import java.util.List;
import java.util.Optional;

public interface CursoRepository extends Repository<Curso, Integer> {
    Optional<Curso> findById(Integer id);
    List<Curso> findAllByOrderByNomeCursoAsc();
}
