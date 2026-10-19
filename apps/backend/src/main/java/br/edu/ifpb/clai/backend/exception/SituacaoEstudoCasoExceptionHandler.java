package br.edu.ifpb.clai.backend.exception;

import br.edu.ifpb.clai.backend.controller.SituacaoEstudoCasoController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = SituacaoEstudoCasoController.class)
public class SituacaoEstudoCasoExceptionHandler {
    @ExceptionHandler(EstudoCasoNaoEncontradoException.class)
    public ResponseEntity<?> naoEncontrado(EstudoCasoNaoEncontradoException ex) {
        return ResponseEntity.status(404).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(EstudoCasoBloqueadoException.class)
    public ResponseEntity<?> conflito(EstudoCasoBloqueadoException ex) {
        return ResponseEntity.status(409).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(EstruturaSituacaoIndisponivelException.class)
    public ResponseEntity<?> estrutura(EstruturaSituacaoIndisponivelException ex) {
        return ResponseEntity.status(503).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(erro -> campos.putIfAbsent(erro.getField(), erro.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("erro", "Preencha o motivo ou a justificativa.", "campos", campos));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> invalido(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> identificador() {
        return ResponseEntity.badRequest().body(Map.of("erro",
                "Informe o número interno do estudo, não a matrícula. Use o filtro por matrícula e selecione o estudo na lista."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> formato() {
        return ResponseEntity.badRequest().body(Map.of("erro", "Envie um corpo JSON válido com motivo ou justificativa."));
    }
}
