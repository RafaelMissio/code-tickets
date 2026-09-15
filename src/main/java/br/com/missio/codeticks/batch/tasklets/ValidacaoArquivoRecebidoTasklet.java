package br.com.missio.codeticks.batch.tasklets;

import br.com.missio.codeticks.exceptions.NomeArquivoInvalidoException;
import br.com.missio.codeticks.validators.NomeArquivoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;

import java.nio.file.Files;
import java.nio.file.Path;

public class
ValidacaoArquivoRecebidoTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(ValidacaoArquivoRecebidoTasklet.class);

    private final Path pastaRecebidos;
    private final String nomeArquivo;

    public ValidacaoArquivoRecebidoTasklet(Path pastaRecebidos, String nomeArquivo) {
        this.pastaRecebidos = pastaRecebidos;
        this.nomeArquivo = nomeArquivo;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        try {
            NomeArquivoValidator.validar(nomeArquivo);
        } catch (NomeArquivoInvalidoException e) {
            log.error(e.getMessage());
            throw e;
        }

        Path arquivo = pastaRecebidos.resolve(nomeArquivo);
        if (!Files.exists(arquivo)) {
            String mensagem = "Arquivo '%s' nao encontrado na pasta de recebidos '%s'"
                    .formatted(nomeArquivo, pastaRecebidos);
            log.error(mensagem);
            throw new NomeArquivoInvalidoException(mensagem);
        }

        log.info("Arquivo '{}' esta na pasta de recebidos", nomeArquivo);
        return RepeatStatus.FINISHED;
    }
}
