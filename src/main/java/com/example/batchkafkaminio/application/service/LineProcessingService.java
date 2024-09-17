package com.example.batchkafkaminio.application.service;

import com.example.batchkafkaminio.domain.model.Line;
import com.example.batchkafkaminio.domain.ports.input.LineProcessor;
import com.example.batchkafkaminio.domain.ports.output.MessagePublisher;
import org.springframework.stereotype.Service;

@Service
public class LineProcessingService implements LineProcessor {

    private final MessagePublisher messagePublisher;

    public LineProcessingService(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void process(Line line) {
        // Lógica de processamento adicional, se necessário
        messagePublisher.publish(line);
    }
}
