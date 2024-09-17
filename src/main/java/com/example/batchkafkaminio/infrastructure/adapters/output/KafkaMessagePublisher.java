package com.example.batchkafkaminio.infrastructure.adapters.output;

import com.example.batchkafkaminio.domain.model.Line;
import com.example.batchkafkaminio.domain.ports.output.MessagePublisher;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


@Component
public class KafkaMessagePublisher implements MessagePublisher {

    private final KafkaProducer<String, String> kafkaProducer;

    public KafkaMessagePublisher() {
        this.kafkaProducer = new KafkaProducer<>(kafkaConfig());
    }

    private Properties kafkaConfig() {
        Properties config = new Properties();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("acks", "1");
        config.put("retries", "3");
        config.put("retry.backoff.ms", "1000");
        config.put("delivery.timeout.ms", "5000");
        config.put("request.timeout.ms", "3000");
        config.put("max.block.ms", "5000");
        return config;
    }

    @Override
    public void publish(Line line) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>("meu-topico", line.getContent());
            Future<RecordMetadata> future = kafkaProducer.send(record);
            // Espera pela conclusão da operação de envio, com timeout
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Lança uma RuntimeException para propagar o erro
            throw new RuntimeException("Falha ao enviar mensagem para o Kafka", e);
        }
    }

    @PreDestroy
    public void close() {
        if (kafkaProducer != null) {
            kafkaProducer.close();
        }
    }
}
