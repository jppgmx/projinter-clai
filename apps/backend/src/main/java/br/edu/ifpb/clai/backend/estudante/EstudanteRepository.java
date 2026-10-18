package br.edu.ifpb.clai.backend.estudante;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class EstudanteRepository {
    private final EntityManager entityManager;

    public EstudanteRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean existe(String matricula) {
        return entityManager.find(Estudante.class, matricula) != null;
    }

    public void cadastrar(Estudante estudante) {
        // Matrícula é atribuída pelo cliente: persist garante INSERT, nunca merge/UPDATE.
        // A PK também impede sobrescrita em duas requisições simultâneas.
        entityManager.persist(estudante);
        entityManager.flush();
    }
}
