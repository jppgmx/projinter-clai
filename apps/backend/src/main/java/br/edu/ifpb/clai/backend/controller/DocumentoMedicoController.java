package br.edu.ifpb.clai.backend.controller;

import br.edu.ifpb.clai.backend.dto.DocumentoResumo;
import br.edu.ifpb.clai.backend.entidade.ArquivoMedico;
import br.edu.ifpb.clai.backend.exception.DocumentoInvalidoException;
import br.edu.ifpb.clai.backend.servico.DocumentoMedicoService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/estudos-caso/{id}/documentos")
public class DocumentoMedicoController {
    private final DocumentoMedicoService service;
    public DocumentoMedicoController(DocumentoMedicoService service) { this.service = service; }
    @GetMapping
    public List<DocumentoResumo> listar(@PathVariable Integer id) { return service.listar(id); }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<DocumentoResumo>> enviar(@PathVariable Integer id, @RequestParam("arquivos") List<MultipartFile> arquivos) throws IOException {
        if (arquivos.isEmpty() || arquivos.size() > 10) throw new DocumentoInvalidoException("Selecione de 1 a 10 arquivos.");
        var validados = new ArrayList<ArquivoMedico>();
        for (var arquivo : arquivos) {
            if (arquivo.getSize() > ArquivoMedico.TAMANHO_MAXIMO) throw new DocumentoInvalidoException("Cada arquivo deve ter no máximo 10 MB.");
            validados.add(ArquivoMedico.validar(arquivo.getOriginalFilename(), arquivo.getBytes()));
        }
        return ResponseEntity.status(201).body(service.enviar(id, validados));
    }
    @GetMapping("/{documento}/visualizar")
    public ResponseEntity<byte[]> visualizar(@PathVariable Integer id, @PathVariable Integer documento) { return resposta(id, documento, true); }
    @GetMapping("/{documento}/download")
    public ResponseEntity<byte[]> baixar(@PathVariable Integer id, @PathVariable Integer documento) { return resposta(id, documento, false); }
    private ResponseEntity<byte[]> resposta(Integer id, Integer documento, boolean visualizar) {
        var dados = service.obter(id, documento);
        boolean inline = visualizar && List.of("application/pdf", "image/jpeg", "image/png").contains(dados.tipoMime());
        var disposition = inline ? ContentDisposition.inline() : ContentDisposition.attachment();
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(dados.tipoMime()))
                .contentLength(dados.conteudo().length).cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.filename(dados.nomeArquivo(), StandardCharsets.UTF_8).build().toString())
                .header("X-Content-Type-Options", "nosniff").body(dados.conteudo());
    }
}
