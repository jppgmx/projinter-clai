package br.edu.ifpb.clai.backend;

import br.edu.ifpb.clai.backend.dto.RegistroEstudante;
import br.edu.ifpb.clai.backend.entidade.Curso;
import br.edu.ifpb.clai.backend.entidade.Estudante;
import br.edu.ifpb.clai.backend.exception.EstudanteNaoEncontradoException;
import br.edu.ifpb.clai.backend.repositorio.CursoRepository;
import br.edu.ifpb.clai.backend.repositorio.EstudanteRepository;
import br.edu.ifpb.clai.backend.servico.ConsultaEstudanteService;
import br.edu.ifpb.clai.backend.servico.EdicaoEstudanteService;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EdicaoEstudanteTests {
    private RegistroEstudante dados(String matricula) {
        return new RegistroEstudante(matricula, "B", "Nome atualizado", "2026.1", "15/04/2005", null, null, null, null, null, null, new RegistroEstudante.CursoSelecionado(1));
    }
    @Test
    void atualizaDadosMantendoMatricula() {
        var repo = mock(EstudanteRepository.class); var cursos = mock(CursoRepository.class);
        var consulta = mock(ConsultaEstudanteService.class); var curso = mock(Curso.class);
        var estudante = new Estudante("001", "A", "Nome anterior", "2026.1", "15/04/2005", null, null, null, null, null, null, curso);
        when(repo.buscarComCurso("001")).thenReturn(Optional.of(estudante));
        when(cursos.findById(1)).thenReturn(Optional.of(curso));
        new EdicaoEstudanteService(repo, cursos, consulta).atualizar("001", dados("001"));
        assertThat(estudante.getMatricula()).isEqualTo("001");
        assertThat(estudante.getNomeEstudante()).isEqualTo("Nome atualizado");
        verify(repo, never()).cadastrar(any());
        verify(consulta).detalhar("001");
    }
    @Test
    void rejeitaTrocaDeMatriculaENaoCriaEstudanteInexistente() {
        var repo = mock(EstudanteRepository.class); var cursos = mock(CursoRepository.class);
        var consulta = mock(ConsultaEstudanteService.class);
        var service = new EdicaoEstudanteService(repo, cursos, consulta);
        assertThatThrownBy(() -> service.atualizar("001", dados("002"))).isInstanceOf(IllegalArgumentException.class);
        when(repo.buscarComCurso("001")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.atualizar("001", dados("001"))).isInstanceOf(EstudanteNaoEncontradoException.class);
        verify(repo, never()).cadastrar(any());
        verifyNoInteractions(cursos, consulta);
    }
}
