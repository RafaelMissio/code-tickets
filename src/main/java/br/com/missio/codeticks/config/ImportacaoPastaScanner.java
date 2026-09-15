package br.com.missio.codeticks.config;

import br.com.missio.codeticks.validators.NomeArquivoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecutionException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Component
public class ImportacaoPastaScanner {

    private static final Logger log = LoggerFactory.getLogger(ImportacaoPastaScanner.class);

    private final JobLauncher jobLauncher;
    private final Job job;
    private final Path pastaRecebidos;

    public ImportacaoPastaScanner(JobLauncher jobLauncher, Job job,
                                   @Value("${importacao.pasta.recebidos}") String pastaRecebidos) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.pastaRecebidos = Path.of(pastaRecebidos);
    }

    @Scheduled(initialDelay = 0, fixedDelayString = "${importacao.polling.intervalMs:60000}")
    public void processarPendentes() {
        if (!Files.isDirectory(pastaRecebidos)) {
            return;
        }

        try (Stream<Path> arquivos = Files.list(pastaRecebidos)) {
            arquivos
                    .filter(Files::isRegularFile)
                    .map(arquivo -> arquivo.getFileName().toString())
                    .filter(NomeArquivoValidator::isValido)
                    .sorted()
                    .forEach(this::executar);
        } catch (IOException e) {
            log.error("Falha ao listar a pasta de recebidos '{}'", pastaRecebidos, e);
        }
    }

    private void executar(String nomeArquivo) {
        JobParameters parametros = new JobParametersBuilder()
                .addString("nomeArquivo", nomeArquivo)
                .toJobParameters();

        try {
            jobLauncher.run(job, parametros);
        } catch (JobInstanceAlreadyCompleteException e) {
            log.warn("Arquivo '{}' ja foi processado anteriormente, ignorando", nomeArquivo);
        } catch (JobExecutionException e) {
            log.error("Falha ao iniciar o job para o arquivo '{}'", nomeArquivo, e);
        }
    }
}
