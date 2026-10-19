package br.edu.ifpb.clai.backend;

import br.edu.ifpb.clai.backend.dto.SituacaoEstudoCaso;
import br.edu.ifpb.clai.backend.exception.EstudoCasoBloqueadoException;
import br.edu.ifpb.clai.backend.exception.EstudoCasoNaoEncontradoException;
import br.edu.ifpb.clai.backend.exception.EstruturaSituacaoIndisponivelException;
import br.edu.ifpb.clai.backend.repositorio.SituacaoEstudoCasoRepository;
import br.edu.ifpb.clai.backend.servico.SituacaoEstudoCasoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SituacaoEstudoCasoServiceTests {
    private SituacaoEstudoCasoRepository repo;
    private SituacaoEstudoCasoService service;

    @BeforeEach
    void preparar() {
        repo = mock(SituacaoEstudoCasoRepository.class);
        service = new SituacaoEstudoCasoService(repo);
    }

    private SituacaoEstudoCaso estado(boolean arquivado, boolean trancado, LocalDate fechamento) {
        return new SituacaoEstudoCaso(7, "00123", arquivado, null, null, trancado, null, null, fechamento);
    }

    @Test
    void arquivaComMotivoNormalizadoELeituraBloqueada() {
        when(repo.buscar(7, true)).thenReturn(Optional.of(estado(false, false, null)));
        when(repo.arquivar(7, "Saída da instituição")).thenReturn(1);
        var resultado = estado(true, false, null);
        when(repo.buscar(7, false)).thenReturn(Optional.of(resultado));
        assertThat(service.arquivar(7, "  Saída da instituição  ")).isEqualTo(resultado);
        var ordem = inOrder(repo);
        ordem.verify(repo).buscar(7, true);
        ordem.verify(repo).arquivar(7, "Saída da instituição");
        ordem.verify(repo).buscar(7, false);
    }

    @Test
    void trancaComJustificativaNormalizada() {
        when(repo.buscar(7, true)).thenReturn(Optional.of(estado(false, false, null)));
        when(repo.trancar(7, "Novo acompanhamento")).thenReturn(1);
        when(repo.buscar(7, false)).thenReturn(Optional.of(estado(false, true, null)));
        assertThat(service.trancar(7, "  Novo acompanhamento  ").trancado()).isTrue();
        verify(repo, never()).arquivar(any(), any());
    }

    @Test
    void rejeitaMotivoEJustificativaEmBrancoSemGravar() {
        assertThatThrownBy(() -> service.arquivar(7, " \n ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.trancar(7, null)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(repo);
    }

    @Test
    void naoRearquivaNemSobrescreveMotivoAnterior() {
        when(repo.buscar(7, true)).thenReturn(Optional.of(estado(true, false, null)));
        assertThatThrownBy(() -> service.arquivar(7, "Outro motivo")).isInstanceOf(EstudoCasoBloqueadoException.class);
        verify(repo, never()).arquivar(any(), any());
    }

    @Test
    void naoTrancaArquivadoTrancadoOuFinalizado() {
        for (var estado : List.of(estado(true, false, null), estado(false, true, null),
                estado(false, false, LocalDate.of(2026, 10, 19)))) {
            when(repo.buscar(7, true)).thenReturn(Optional.of(estado));
            assertThatThrownBy(() -> service.trancar(7, "Justificativa")).isInstanceOf(EstudoCasoBloqueadoException.class);
        }
        verify(repo, never()).trancar(any(), any());
    }

    @Test
    void arquivamentoNaoDependeDeEstudoEstarEmAndamentoComoNaReferencia() {
        var finalizado = LocalDate.of(2026, 10, 19);
        when(repo.buscar(7, true)).thenReturn(Optional.of(estado(false, true, finalizado)));
        when(repo.arquivar(7, "Encerramento")).thenReturn(1);
        when(repo.buscar(7, false)).thenReturn(Optional.of(estado(true, true, finalizado)));
        var resultado = service.arquivar(7, "Encerramento");
        assertThat(resultado.trancado()).isTrue();
        assertThat(resultado.dataFechamento()).isEqualTo(finalizado);
        verify(repo, never()).trancar(any(), any());
    }

    @Test
    void inexistenteNaoCriaRegistro() {
        when(repo.buscar(7, true)).thenReturn(Optional.empty());
        when(repo.buscar(7, false)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.consultar(7)).isInstanceOf(EstudoCasoNaoEncontradoException.class);
        assertThatThrownBy(() -> service.arquivar(7, "Motivo")).isInstanceOf(EstudoCasoNaoEncontradoException.class);
        assertThatThrownBy(() -> service.trancar(7, "Justificativa")).isInstanceOf(EstudoCasoNaoEncontradoException.class);
        verify(repo, never()).arquivar(any(), any());
        verify(repo, never()).trancar(any(), any());
    }

    @Test
    void conflitoDeGravacaoNaoRetornaSucesso() {
        when(repo.buscar(7, true)).thenReturn(Optional.of(estado(false, false, null)));
        when(repo.trancar(7, "Justificativa")).thenReturn(0);
        assertThatThrownBy(() -> service.trancar(7, "Justificativa")).isInstanceOf(EstudoCasoBloqueadoException.class);
        verify(repo, never()).buscar(7, false);
    }

    @Test
    void desarquivaComBloqueioPreservandoTrancamentoEFechamento() {
        var fechamento = LocalDate.of(2026, 10, 19);
        when(repo.buscar(7, true)).thenReturn(Optional.of(estado(true, true, fechamento)));
        when(repo.desarquivar(7)).thenReturn(1);
        var resultado = estado(false, true, fechamento);
        when(repo.buscar(7, false)).thenReturn(Optional.of(resultado));
        assertThat(service.desarquivar(7)).isEqualTo(resultado);
        var ordem = inOrder(repo);
        ordem.verify(repo).buscar(7, true);
        ordem.verify(repo).desarquivar(7);
        ordem.verify(repo).buscar(7, false);
        verify(repo, never()).trancar(any(), any());
    }

    @Test
    void naoDesarquivaEstudoAtivoOuInexistente() {
        when(repo.buscar(7, true)).thenReturn(Optional.of(estado(false, false, null)));
        assertThatThrownBy(() -> service.desarquivar(7)).isInstanceOf(EstudoCasoBloqueadoException.class);
        when(repo.buscar(7, true)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.desarquivar(7)).isInstanceOf(EstudoCasoNaoEncontradoException.class);
        verify(repo, never()).desarquivar(any());
    }

    @Test
    void desarquivamentoSemRegistroAtualizadoEhConflito() {
        when(repo.buscar(7, true)).thenReturn(Optional.of(estado(true, false, null)));
        when(repo.desarquivar(7)).thenReturn(0);
        assertThatThrownBy(() -> service.desarquivar(7)).isInstanceOf(EstudoCasoBloqueadoException.class);
        verify(repo, never()).buscar(7, false);
    }

    @Test
    void propagaFaltaDeColunasSemInventarPersistenciaAlternativa() {
        when(repo.buscar(7, true)).thenReturn(Optional.of(estado(false, false, null)));
        when(repo.arquivar(7, "Motivo")).thenThrow(new EstruturaSituacaoIndisponivelException(List.of("motivo_arquivamento")));
        assertThatThrownBy(() -> service.arquivar(7, "Motivo"))
                .isInstanceOf(EstruturaSituacaoIndisponivelException.class).hasMessageContaining("motivo_arquivamento");
        verify(repo, never()).buscar(7, false);
    }
}
