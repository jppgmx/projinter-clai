package br.edu.ifpb.clai.backend.estudante;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.util.LinkedHashMap;
import java.util.Map;

// Escopo restrito ao cadastro: não substitui o tratamento global da tarefa T04.
@RestControllerAdvice(assignableTypes = EstudanteController.class)
public class RegistroEstudanteExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validar(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(erro ->
                campos.putIfAbsent(erro.getField(), erro.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of(
                "erro", "Revise os campos informados.", "campos", campos));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> negocio(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("erro", ex.getReason()));
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
