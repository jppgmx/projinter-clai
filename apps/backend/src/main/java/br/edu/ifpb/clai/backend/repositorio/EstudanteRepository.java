package br.edu.ifpb.clai.backend.repositorio;

import br.edu.ifpb.clai.backend.entidade.Estudante;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class EstudanteRepository {
    private final EntityManager entityManager;

    public EstudanteRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean existe(String matricula) {
        return entityManager.find(Estudante.class, matricula) != null;
    }

    public List<Estudante> consultar(String busca) {
        return entityManager.createQuery("""
                select e from Estudante e join fetch e.curso
                where :busca = ''
                   or locate(:busca, lower(e.nomeEstudante)) > 0
                   or locate(:busca, lower(e.matricula)) > 0
                order by lower(e.nomeEstudante), e.matricula
                """, Estudante.class)
                .setParameter("busca", busca)
                .getResultList();
    }

    public Optional<Estudante> buscarComCurso(String matricula) {
        return entityManager.createQuery("""
                select e from Estudante e join fetch e.curso
                where e.matricula = :matricula
                """, Estudante.class)
                .setParameter("matricula", matricula)
                .getResultStream().findFirst();
    }

    public void cadastrar(Estudante estudante) {
        // Matrícula é atribuída pelo cliente: persist garante INSERT, nunca merge/UPDATE.
        // A PK também impede sobrescrita em duas requisições simultâneas.
        entityManager.persist(estudante);
        entityManager.flush();
    }
}
