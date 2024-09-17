package com.example.batchkafkaminio.infrastructure.config;

import com.example.batchkafkaminio.domain.model.Line;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, Line> producerFactory() {
        DefaultKafkaProducerFactory<String, Line> factory = new DefaultKafkaProducerFactory<>(producerConfigs());
        factory.setTransactionIdPrefix("tx-");
        return factory;
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Configurações adicionais necessárias para idempotência
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "tx-batch");

        // Configurações para limitar retentativas e tempos de espera
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3); // Número máximo de retentativas
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000); // Intervalo entre retentativas
        configProps.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 5000); // Tempo máximo para bloqueios
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 30000); // Tempo máximo para entrega

        return configProps;
    }

    @Bean
    public KafkaTemplate<String, Line> kafkaTemplate() {
        KafkaTemplate<String, Line> template = new KafkaTemplate<>(producerFactory());
        template.setDefaultTopic("meu-topico");
        return template;
    }

}
