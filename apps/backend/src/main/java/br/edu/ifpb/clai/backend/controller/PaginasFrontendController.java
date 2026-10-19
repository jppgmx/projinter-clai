package br.edu.ifpb.clai.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Encaminha somente as rotas React existentes; /api permanece nos controllers REST. */
@Controller
public class PaginasFrontendController {
    @GetMapping({"/login", "/alunos", "/alunos/{alunoId}", "/planos-estudos/estudos"})
    public String pagina() { return "forward:/index.html"; }
}
