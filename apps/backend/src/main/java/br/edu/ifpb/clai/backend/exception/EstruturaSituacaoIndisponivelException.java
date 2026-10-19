package br.edu.ifpb.clai.backend.exception;

import java.util.List;

public class EstruturaSituacaoIndisponivelException extends RuntimeException {
    public EstruturaSituacaoIndisponivelException(List<String> colunas) {
        super("Operação indisponível: faltam colunas de estudo_de_caso previstas na referência: "
                + String.join(", ", colunas) + ". Nenhuma coluna foi criada pela aplicação.");
    }
}
