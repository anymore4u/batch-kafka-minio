package com.example.batchkafkaminio.application.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importJob;

    @Autowired
    private JobExplorer jobExplorer;

    @PostMapping("/start")
    public String startJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(importJob, jobParameters);
        return "Job started with ID: " + jobExecution.getJobId();
    }

    @PostMapping("/restart")
    public String restartJob() throws Exception {
        // Obter a última instância do job
        JobInstance lastJobInstance = jobExplorer.getLastJobInstance("importJob");
        if (lastJobInstance == null) {
            return "No job instance found to restart.";
        }

        // Obter todas as execuções do job
        List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(lastJobInstance);
        if (jobExecutions == null || jobExecutions.isEmpty()) {
            return "No job executions found for the last job instance.";
        }

        // Encontrar a última execução falhada
        JobExecution lastFailedJobExecution = jobExecutions.stream()
                .filter(jobExecution -> jobExecution.getStatus() == BatchStatus.FAILED)
                .max(Comparator.comparing(JobExecution::getCreateTime))
                .orElse(null);

        if (lastFailedJobExecution == null) {
            return "No failed job execution found to restart.";
        }

        // Reiniciar o job com os mesmos parâmetros
        JobParameters jobParameters = lastFailedJobExecution.getJobParameters();
        JobExecution jobExecution = jobLauncher.run(importJob, jobParameters);
        return "Job restarted with ID: " + jobExecution.getJobId();
    }
}
