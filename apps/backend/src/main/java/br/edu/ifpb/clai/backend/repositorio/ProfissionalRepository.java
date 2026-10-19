package br.edu.ifpb.clai.backend.repositorio;

import br.edu.ifpb.clai.backend.entidade.Profissional;
import org.springframework.data.repository.Repository;
import java.util.List;
import java.util.Optional;

// Somente leitura dos profissionais existentes, para selecionar o responsável na US05.
public interface ProfissionalRepository extends Repository<Profissional, Integer> {
    Optional<Profissional> findById(Integer id);
    List<Profissional> findAllByOrderByNomeProfissionalAsc();
}
