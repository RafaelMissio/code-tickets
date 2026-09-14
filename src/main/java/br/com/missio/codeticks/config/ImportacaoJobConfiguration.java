package br.com.missio.codeticks.config;

import br.com.missio.codeticks.entities.Importacao;
import br.com.missio.codeticks.processors.ImportacaoItemProcessor;
import br.com.missio.codeticks.readers.ImportacaoItemReader;
import br.com.missio.codeticks.writers.ImportacaoItemWriter;
import org.springframework.batch.core.configuration.support.JdbcDefaultBatchConfiguration;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
public class ImportacaoJobConfiguration extends JdbcDefaultBatchConfiguration {

    private final PlatformTransactionManager transactionManager;

    public ImportacaoJobConfiguration(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job job(JobRepository jobRepository, Step stepInicial) {
        return new JobBuilder("geracao-tickets", jobRepository)
                .start(stepInicial)
                .incrementer(new RunIdIncrementer())
                .build();
    }


    @Bean
    public Step stepInicial(JobRepository jobRepository,
                            ItemReader<Importacao> reader,
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
    public ItemReader<Importacao> reader(@Value("${importacao.csv.path}")
                                             String importacaoCsvPath) {
        return ImportacaoItemReader.build(importacaoCsvPath);
    }

    @Bean
    public ItemWriter<Importacao> writer(DataSource dataSource) {
        return ImportacaoItemWriter.build(dataSource);
    }


}
