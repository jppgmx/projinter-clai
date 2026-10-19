package br.edu.ifpb.clai.backend.controller;

import br.edu.ifpb.clai.backend.dto.AtualizacaoEstudoCaso;
import br.edu.ifpb.clai.backend.dto.EstudoCasoEdicao;
import br.edu.ifpb.clai.backend.dto.ResumoEstudoCaso;
import java.util.List;
import br.edu.ifpb.clai.backend.servico.AtualizacaoEstudoCasoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estudos-caso")
public class AtualizacaoEstudoCasoController {
    private final AtualizacaoEstudoCasoService service;

    public AtualizacaoEstudoCasoController(AtualizacaoEstudoCasoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ResumoEstudoCaso> listar(@RequestParam(required = false) String matricula) {
        return service.listar(matricula);
    }

    @GetMapping("/{id}")
    public EstudoCasoEdicao consultar(@PathVariable Integer id) {
        return service.consultar(id);
    }

    @PutMapping("/{id}")
    public EstudoCasoEdicao atualizar(@PathVariable Integer id, @RequestBody AtualizacaoEstudoCaso dados) {
        return service.atualizar(id, dados);
    }
}
