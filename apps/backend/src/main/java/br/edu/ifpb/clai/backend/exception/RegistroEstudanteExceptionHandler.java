package br.edu.ifpb.clai.backend.exception;

import br.edu.ifpb.clai.backend.controller.EstudanteController;
import br.edu.ifpb.clai.backend.controller.ConsultaEstudanteController;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.LinkedHashMap;
import java.util.Map;

// Escopo restrito ao cadastro e à consulta de estudantes; não substitui a tarefa T04.
@RestControllerAdvice(assignableTypes = {EstudanteController.class, ConsultaEstudanteController.class})
public class RegistroEstudanteExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validar(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(erro ->
                campos.putIfAbsent(erro.getField(), erro.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of(
                "erro", "Revise os campos informados.", "campos", campos));
    }

    @ExceptionHandler(MatriculaDuplicadaException.class)
    public ResponseEntity<?> matriculaDuplicada(MatriculaDuplicadaException ex) {
        return ResponseEntity.status(409).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(CursoInvalidoException.class)
    public ResponseEntity<?> cursoInvalido(CursoInvalidoException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(EstudanteNaoEncontradoException.class)
    public ResponseEntity<?> estudanteNaoEncontrado(EstudanteNaoEncontradoException ex) {
        return ResponseEntity.status(404).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> jsonInvalido() {
        return ResponseEntity.badRequest().body(Map.of("erro", "Dados do cadastro inválidos."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflito() {
        return ResponseEntity.status(409).body(Map.of("erro",
                "O cadastro conflitou com os dados existentes. Verifique a matrícula e o curso."));
    }
}
