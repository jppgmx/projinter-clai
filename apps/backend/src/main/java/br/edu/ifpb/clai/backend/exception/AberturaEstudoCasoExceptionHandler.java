package br.edu.ifpb.clai.backend.exception;

import br.edu.ifpb.clai.backend.controller.AberturaEstudoCasoController;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = AberturaEstudoCasoController.class)
public class AberturaEstudoCasoExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validar(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(erro -> {
            String campo = "origemOutroInformada".equals(erro.getField())
                    ? "origemEncaminhamentoOutro" : erro.getField();
            campos.putIfAbsent(campo, erro.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(Map.of("erro", "Revise os campos informados.", "campos", campos));
    }

    @ExceptionHandler(EstudanteNaoEncontradoException.class)
    public ResponseEntity<?> estudanteInexistente(EstudanteNaoEncontradoException ex) {
        return ResponseEntity.status(404).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(ProfissionalNaoEncontradoException.class)
    public ResponseEntity<?> profissionalInvalido(ProfissionalNaoEncontradoException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage(), "campos",
                Map.of("idProfissionalResponsavel", ex.getMessage())));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> jsonInvalido() {
        return ResponseEntity.badRequest().body(Map.of("erro", "Dados de abertura inválidos."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflito() {
        return ResponseEntity.status(409).body(Map.of("erro",
                "Não foi possível abrir o estudo com os dados atuais. Confira o estudante e o profissional antes de reenviar."));
    }
}
