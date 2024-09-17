package com.example.batchkafkaminio;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BatchKafkaMinioApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchKafkaMinioApplication.class, args);
    }

    @Bean
    public CommandLineRunner runJob(JobLauncher jobLauncher, Job importJob, ConfigurableApplicationContext context) {
        return args -> {
            JobExecution jobExecution = jobLauncher.run(importJob, new JobParametersBuilder()
                    .toJobParameters());

            // Captura o status do job
            ExitStatus exitStatus = jobExecution.getExitStatus();

            // Fecha o contexto da aplicação
            int exitCode = exitStatus.equals(ExitStatus.COMPLETED) ? 0 : 1;
            SpringApplication.exit(context, () -> exitCode);
        };
    }

}
