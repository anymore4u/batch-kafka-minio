package com.example.batchkafkaminio.domain.ports.input;

import com.example.batchkafkaminio.domain.model.Line;

public interface LineProcessor {
    void process(Line line);
}
