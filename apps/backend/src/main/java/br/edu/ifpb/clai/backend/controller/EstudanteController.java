package br.edu.ifpb.clai.backend.controller;

import br.edu.ifpb.clai.backend.dto.CursoEstudante;
import br.edu.ifpb.clai.backend.entidade.Estudante;
import br.edu.ifpb.clai.backend.servico.CursoService;
import br.edu.ifpb.clai.backend.servico.EstudanteService;
import br.edu.ifpb.clai.backend.dto.RegistroEstudante;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EstudanteController {
    private final EstudanteService service;
    private final CursoService cursos;

    public EstudanteController(EstudanteService service, CursoService cursos) {
        this.service = service;
        this.cursos = cursos;
    }

    @PostMapping("/estudantes")
    public ResponseEntity<Estudante> registrar(@Valid @RequestBody RegistroEstudante dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dados));
    }

    // Somente leitura dos cursos existentes para preencher o cadastro.
    @GetMapping("/cursos")
    public List<CursoEstudante> cursos() {
        return cursos.listar();
    }
}
