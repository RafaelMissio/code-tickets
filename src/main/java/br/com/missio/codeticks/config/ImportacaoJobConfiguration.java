package br.com.missio.codeticks.config;

import br.com.missio.codeticks.entities.Importacao;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ImportacaoJobConfiguration {

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
                            ItemWriter<Importacao> writer){
        return new StepBuilder("stepInicial", jobRepository)
                .<Importacao, Importacao>chunk(200)
                .transactionManager(transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public ItemReader<Importacao> reader(@Value("${IMPORTACAO_CSV_PATH}")
                                             String importacaoCsvPath) {
        return new FlatFileItemReaderBuilder<Importacao>()
                .name("leitura-csv")
                .resource(new FileSystemResource(importacaoCsvPath))
                .comments("--")
                .delimited()
                .names("cpf", "cliente", "nascimento", "evento", "data", "tipoIngresso", "valor")
                .targetType(Importacao.class)
                .build();
    }





}
