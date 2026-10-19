package br.edu.ifpb.clai.backend.controller;

import br.edu.ifpb.clai.backend.dto.DetalhesEstudante;
import br.edu.ifpb.clai.backend.dto.ResumoEstudante;
import br.edu.ifpb.clai.backend.servico.ConsultaEstudanteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/estudantes")
public class ConsultaEstudanteController {
    private final ConsultaEstudanteService service;

    public ConsultaEstudanteController(ConsultaEstudanteService service) {
        this.service = service;
    }

    @GetMapping
    public List<ResumoEstudante> consultar(@RequestParam(required = false) String busca) {
        return service.consultar(busca);
    }

    @GetMapping("/{matricula}")
    public DetalhesEstudante detalhar(@PathVariable String matricula) {
        return service.detalhar(matricula);
    }
}
