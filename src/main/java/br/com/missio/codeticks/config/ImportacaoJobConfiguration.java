package br.com.missio.codeticks.config;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportacaoJobConfiguration {


    @Bean
    public Job job(JobRepository jobRepository, Step stepInicial) {
        return new JobBuilder("geracao-tickets", jobRepository)
                .start(stepInicial)
                .incrementer(new RunIdIncrementer())
                .build();
    }


}
