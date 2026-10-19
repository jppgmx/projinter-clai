package br.edu.ifpb.clai.backend.exception;

import br.edu.ifpb.clai.backend.controller.DocumentoMedicoController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import java.io.IOException;
import java.util.Map;

@RestControllerAdvice(assignableTypes = DocumentoMedicoController.class)
public class DocumentoMedicoExceptionHandler {
    @ExceptionHandler({EstudoCasoNaoEncontradoException.class, DocumentoNaoEncontradoException.class})
    public ResponseEntity<?> inexistente(RuntimeException ex) { return erro(404, ex.getMessage()); }
    @ExceptionHandler(DocumentoInvalidoException.class)
    public ResponseEntity<?> invalido(DocumentoInvalidoException ex) { return erro(400, ex.getMessage()); }
    @ExceptionHandler(EstudoCasoBloqueadoException.class)
    public ResponseEntity<?> bloqueado(EstudoCasoBloqueadoException ex) { return erro(409, ex.getMessage()); }
    @ExceptionHandler(DocumentoEstruturaException.class)
    public ResponseEntity<?> estrutura(DocumentoEstruturaException ex) { return erro(503, ex.getMessage()); }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> tamanho() { return erro(413, "O envio excede o limite permitido. Cada arquivo aceita até 10 MB."); }
    @ExceptionHandler({MissingServletRequestPartException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<?> faltante() { return erro(400, "Selecione os arquivos para enviar."); }
    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> leitura() { return erro(400, "Não foi possível ler os arquivos enviados."); }
    private ResponseEntity<?> erro(int status, String mensagem) { return ResponseEntity.status(status).body(Map.of("erro", mensagem)); }
}
