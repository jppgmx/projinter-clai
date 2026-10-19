package br.edu.ifpb.clai.backend.exception;

import br.edu.ifpb.clai.backend.controller.AtualizacaoEstudoCasoController;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice(assignableTypes = AtualizacaoEstudoCasoController.class)
public class AtualizacaoEstudoCasoExceptionHandler {
    @ExceptionHandler(EstudoCasoNaoEncontradoException.class)
    public ResponseEntity<?> naoEncontrado(EstudoCasoNaoEncontradoException ex) {
        return ResponseEntity.status(404).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(EstudoCasoBloqueadoException.class)
    public ResponseEntity<?> bloqueado(EstudoCasoBloqueadoException ex) {
        return ResponseEntity.status(409).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(AtualizacaoEstudoCasoInvalidaException.class)
    public ResponseEntity<?> invalido(AtualizacaoEstudoCasoInvalidaException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage(), "campos", ex.getCampos()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> jsonInvalido() {
        return ResponseEntity.badRequest().body(Map.of("erro", "Dados de atualização inválidos."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflito() {
        return ResponseEntity.status(409).body(Map.of("erro",
                "A atualização conflitou com os dados atuais. Recarregue o estudo e confira os profissionais."));
    }
}
