package com.example.batchkafkaminio.infrastructure.config;

import com.example.batchkafkaminio.application.service.LineProcessingService;
import com.example.batchkafkaminio.domain.model.Line;
import com.example.batchkafkaminio.domain.ports.output.MessagePublisher;
import com.example.batchkafkaminio.infrastructure.adapters.input.MinIOFileReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.kafka.KafkaItemWriter;
import org.springframework.batch.item.kafka.builder.KafkaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import software.amazon.awssdk.services.s3.S3AsyncClient;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final LineProcessingService lineProcessingService;
    private final S3AsyncClient s3Client;
    private final MessagePublisher messagePublisher;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private KafkaTemplate<String, Line> kafkaTemplate;

    @Autowired
    public BatchConfig(JobRepository jobRepository,
                       PlatformTransactionManager transactionManager,
                       LineProcessingService lineProcessingService,
                       S3AsyncClient s3Client,
                       MessagePublisher messagePublisher) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.lineProcessingService = lineProcessingService;
        this.s3Client = s3Client;
        this.messagePublisher = messagePublisher;
    }

    @Bean
    public MinIOFileReader itemReader() throws Exception {
        return new MinIOFileReader(s3Client, "meu-bucket", "output.txt");
    }

    @Bean
    public ItemProcessor<Line, Line> itemProcessor() {
        return line -> {
            // Realize transformações necessárias
            return line;
        };
    }

    @Bean
    public ItemWriter<Line> itemWriter() {
        KafkaItemWriter<String, Line> writer = new KafkaItemWriterBuilder<String, Line>()
                .kafkaTemplate(kafkaTemplate)
                .itemKeyMapper(line -> null) // Use null se não tiver chave
                // Remover ou manter itemTopicResolver conforme necessidade
                .build();
        return writer;
    }

    @Bean
    public StepBuilder stepBuilder() {
        return new StepBuilder("processStep", jobRepository);
    }

    @Bean
    public TaskletStep processStep() throws Exception {
        return stepBuilder()
                .<Line, Line>chunk(10, transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job importJob() throws Exception {
        return new JobBuilder("importJob", jobRepository)
                .start(processStep())
                .build();
    }

}
