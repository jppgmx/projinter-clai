package br.edu.ifpb.clai.backend.controller;

import br.edu.ifpb.clai.backend.dto.AberturaEstudoCaso;
import br.edu.ifpb.clai.backend.dto.EstudoCasoAberto;
import br.edu.ifpb.clai.backend.dto.ProfissionalResponsavel;
import br.edu.ifpb.clai.backend.servico.AberturaEstudoCasoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/estudos-caso")
public class AberturaEstudoCasoController {
    private final AberturaEstudoCasoService service;

    public AberturaEstudoCasoController(AberturaEstudoCasoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EstudoCasoAberto> abrir(@Valid @RequestBody AberturaEstudoCaso dados) {
        return ResponseEntity.status(201).body(service.abrir(dados));
    }

    @GetMapping("/profissionais")
    public List<ProfissionalResponsavel> profissionais() {
        return service.listarProfissionais();
    }
}
