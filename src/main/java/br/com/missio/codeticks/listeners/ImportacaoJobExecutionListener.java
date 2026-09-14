package br.com.missio.codeticks.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ImportacaoJobExecutionListener implements JobExecutionListener {

    private static final String STEP_VALIDACAO_ARQUIVO = "stepValidacaoArquivo";

    private static final Logger log = LoggerFactory.getLogger(ImportacaoJobExecutionListener.class);

    private final Path pastaRecebidos;
    private final Path pastaProcessados;
    private final Path pastaErro;
    private final String nomeArquivo;

    public ImportacaoJobExecutionListener(Path pastaRecebidos, Path pastaProcessados, Path pastaErro, String nomeArquivo) {
        this.pastaRecebidos = pastaRecebidos;
        this.pastaProcessados = pastaProcessados;
        this.pastaErro = pastaErro;
        this.nomeArquivo = nomeArquivo;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            moverArquivo(pastaProcessados, "Arquivo '{}' processado com sucesso, movido para a pasta de processados");
            return;
        }

        if (falhouNaValidacaoDoArquivo(jobExecution)) {
            // Falha de nome/presenca do arquivo nao se resolve reexecutando o job com os
            // mesmos parametros, entao o arquivo e movido para a pasta de erro para analise manual.
            moverArquivo(pastaErro, "Arquivo '{}' nao foi processado, movido para a pasta de erro");
            return;
        }

        log.error("Job falhou ao processar o arquivo '{}'. O arquivo permanece na pasta de recebidos para que a "
                + "aplicacao retome a execucao do ponto em que parou na proxima inicializacao", nomeArquivo);
    }

    private boolean falhouNaValidacaoDoArquivo(JobExecution jobExecution) {
        return jobExecution.getStepExecutions().stream()
                .anyMatch(stepExecution -> STEP_VALIDACAO_ARQUIVO.equals(stepExecution.getStepName())
                        && stepExecution.getStatus() == BatchStatus.FAILED);
    }

    private void moverArquivo(Path pastaDestino, String mensagemSucesso) {
        Path origem = pastaRecebidos.resolve(nomeArquivo);
        if (!Files.exists(origem)) {
            return;
        }

        try {
            Files.createDirectories(pastaDestino);
            Files.move(origem, pastaDestino.resolve(nomeArquivo), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Falha ao mover arquivo '{}' para a pasta '{}'", nomeArquivo, pastaDestino, e);
            return;
        }

        log.info(mensagemSucesso, nomeArquivo);
    }
}
