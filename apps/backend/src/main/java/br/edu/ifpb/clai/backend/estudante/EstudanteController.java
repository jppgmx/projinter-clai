package br.edu.ifpb.clai.backend.estudante;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EstudanteController {
    private final EstudanteService service;
    private final CursoRepository cursos;

    public EstudanteController(EstudanteService service, CursoRepository cursos) {
        this.service = service;
        this.cursos = cursos;
    }

    @PostMapping("/estudantes")
    public ResponseEntity<Estudante> registrar(@Valid @RequestBody RegistroEstudante dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dados));
    }

    // Somente leitura dos cursos existentes para preencher o cadastro.
    @GetMapping("/cursos")
    public List<Curso> cursos() {
        return cursos.findAllByOrderByNomeCursoAsc();
    }
}
