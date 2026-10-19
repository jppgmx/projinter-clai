package br.edu.ifpb.clai.backend.controller;

import br.edu.ifpb.clai.backend.dto.ArquivamentoEstudoCaso;
import br.edu.ifpb.clai.backend.dto.TrancamentoEstudoCaso;
import br.edu.ifpb.clai.backend.dto.SituacaoEstudoCaso;
import br.edu.ifpb.clai.backend.servico.SituacaoEstudoCasoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estudos-caso/{id}")
public class SituacaoEstudoCasoController {
    private final SituacaoEstudoCasoService service;

    public SituacaoEstudoCasoController(SituacaoEstudoCasoService service) {
        this.service = service;
    }

    @GetMapping("/situacao")
    public SituacaoEstudoCaso consultar(@PathVariable Integer id) {
        return service.consultar(id);
    }

    @PatchMapping("/arquivar")
    public SituacaoEstudoCaso arquivar(@PathVariable Integer id, @Valid @RequestBody ArquivamentoEstudoCaso dados) {
        return service.arquivar(id, dados.motivo());
    }

    @PatchMapping("/desarquivar")
    public SituacaoEstudoCaso desarquivar(@PathVariable Integer id) {
        return service.desarquivar(id);
    }

    @PatchMapping("/trancar")
    public SituacaoEstudoCaso trancar(@PathVariable Integer id, @Valid @RequestBody TrancamentoEstudoCaso dados) {
        return service.trancar(id, dados.justificativa());
    }
}
