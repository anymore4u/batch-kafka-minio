// infrastructure/adapters/input/MinIOFileReader.java
package com.example.batchkafkaminio.infrastructure.adapters.input;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.example.batchkafkaminio.domain.model.Line;
import org.springframework.batch.item.ItemReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MinIOFileReader implements ItemReader<Line> {

    private BufferedReader reader;

    public MinIOFileReader(AmazonS3 s3Client, String bucketName, String key) {
        S3Object s3object = s3Client.getObject(bucketName, key);
        InputStreamReader streamReader = new InputStreamReader(s3object.getObjectContent());
        this.reader = new BufferedReader(streamReader);
    }

    @Override
    public Line read() throws Exception {
        String content = reader.readLine();
        if (content != null) {
            return new Line(content);
        } else {
            return null;
        }
    }
}
