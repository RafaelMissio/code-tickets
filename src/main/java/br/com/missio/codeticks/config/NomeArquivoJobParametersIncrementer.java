package br.com.missio.codeticks.config;

import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.job.parameters.JobParametersIncrementer;

/**
 * Mantem o mesmo parametro identificador (nomeArquivo) entre execucoes.
 * Isso faz com que reiniciar a aplicacao apos uma falha reaproveite a mesma
 * JobInstance e o Spring Batch retome a execucao do ponto em que parou, em vez
 * de criar uma nova instancia a cada start (como fazia o RunIdIncrementer).
 * Um arquivo com nome diferente naturalmente gera uma nova JobInstance.
 */
public class NomeArquivoJobParametersIncrementer implements JobParametersIncrementer {

    private final String nomeArquivo;

    public NomeArquivoJobParametersIncrementer(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    @Override
    public JobParameters getNext(JobParameters parameters) {
        return new JobParametersBuilder()
                .addString("nomeArquivo", nomeArquivo)
                .toJobParameters();
    }
}
