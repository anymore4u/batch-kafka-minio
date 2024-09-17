package com.example.batchkafkaminio.domain.ports.output;

import com.example.batchkafkaminio.domain.model.Line;

public interface MessagePublisher {
    void publish(Line line);
}
