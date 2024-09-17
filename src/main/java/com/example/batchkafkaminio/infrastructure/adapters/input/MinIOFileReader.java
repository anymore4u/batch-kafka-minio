package com.example.batchkafkaminio.infrastructure.adapters.input;

import com.example.batchkafkaminio.domain.model.Line;
import org.springframework.batch.item.ItemReader;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

public class MinIOFileReader implements ItemReader<Line> {

    private final BufferedReader reader;

    public MinIOFileReader(S3AsyncClient s3Client, String bucketName, String key) throws Exception {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        CompletableFuture<ResponseBytes<GetObjectResponse>> future = s3Client.getObject(getObjectRequest, software.amazon.awssdk.core.async.AsyncResponseTransformer.toBytes());

        ResponseBytes<GetObjectResponse> objectBytes = future.get();
        InputStream inputStream = objectBytes.asInputStream();
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
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
