package br.edu.ifpb.clai.backend.controller;

import br.edu.ifpb.clai.backend.dto.RegistroEstudante;
import br.edu.ifpb.clai.backend.dto.DetalhesEstudante;
import br.edu.ifpb.clai.backend.servico.EdicaoEstudanteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estudantes")
public class EdicaoEstudanteController {
    private final EdicaoEstudanteService service;
    public EdicaoEstudanteController(EdicaoEstudanteService service) { this.service = service; }

    @PutMapping("/{matricula}")
    public DetalhesEstudante atualizar(@PathVariable String matricula, @Valid @RequestBody RegistroEstudante dados) {
        return service.atualizar(matricula, dados);
    }
}
