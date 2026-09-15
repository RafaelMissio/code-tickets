package br.com.missio.codeticks.config;

import br.com.missio.codeticks.entities.Importacao;
import br.com.missio.codeticks.listeners.ImportacaoJobExecutionListener;
import br.com.missio.codeticks.batch.processors.ImportacaoItemProcessor;
import br.com.missio.codeticks.batch.readers.ImportacaoItemReader;
import br.com.missio.codeticks.batch.tasklets.ValidacaoArquivoRecebidoTasklet;
import br.com.missio.codeticks.batch.writers.ImportacaoItemWriter;
import org.springframework.batch.core.configuration.support.JdbcDefaultBatchConfiguration;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemStreamReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.nio.file.Path;

@Configuration
public class ImportacaoJobConfiguration extends JdbcDefaultBatchConfiguration {

    private final PlatformTransactionManager transactionManager;

    public ImportacaoJobConfiguration(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job job(JobRepository jobRepository,
                   Step stepValidacaoArquivo,
                   Step stepInicial,
                   ImportacaoJobExecutionListener jobExecutionListener) {
        return new JobBuilder("geracao-tickets", jobRepository)
                .listener(jobExecutionListener)
                .start(stepValidacaoArquivo)
                .next(stepInicial)
                .build();
    }

    @Bean
    public Step stepValidacaoArquivo(JobRepository jobRepository, Tasklet validacaoArquivoRecebidoTasklet) {
        return new StepBuilder("stepValidacaoArquivo", jobRepository)
                .tasklet(validacaoArquivoRecebidoTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet validacaoArquivoRecebidoTasklet(@Value("${importacao.pasta.recebidos}") String pastaRecebidos,
                                                     @Value("#{jobParameters['nomeArquivo']}") String nomeArquivo) {
        return new ValidacaoArquivoRecebidoTasklet(Path.of(pastaRecebidos), nomeArquivo);
    }

    @Bean
    public Step stepInicial(JobRepository jobRepository,
                            ItemStreamReader<Importacao> reader,
                            ItemProcessor<Importacao, Importacao> processor,
                            ItemWriter<Importacao> writer){
        return new StepBuilder("stepInicial", jobRepository)
                .<Importacao, Importacao>chunk(200)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean
    public ImportacaoItemProcessor processor() {
        return new ImportacaoItemProcessor();
    }

    @Bean
    @StepScope
    public ItemStreamReader<Importacao> reader(@Value("${importacao.pasta.recebidos}") String pastaRecebidos,
                                          @Value("#{jobParameters['nomeArquivo']}") String nomeArquivo) {
        return ImportacaoItemReader.build(Path.of(pastaRecebidos).resolve(nomeArquivo).toString());
    }

    @Bean
    public ItemWriter<Importacao> writer(DataSource dataSource) {
        return ImportacaoItemWriter.build(dataSource);
    }

    @Bean
    @JobScope
    public ImportacaoJobExecutionListener jobExecutionListener(
            @Value("${importacao.pasta.recebidos}") String pastaRecebidos,
            @Value("${importacao.pasta.processados}") String pastaProcessados,
            @Value("${importacao.pasta.erro}") String pastaErro,
            @Value("#{jobParameters['nomeArquivo']}") String nomeArquivo) {
        return new ImportacaoJobExecutionListener(
                Path.of(pastaRecebidos), Path.of(pastaProcessados), Path.of(pastaErro), nomeArquivo);
    }

}
