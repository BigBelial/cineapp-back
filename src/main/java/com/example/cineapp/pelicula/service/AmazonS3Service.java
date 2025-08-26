package com.example.cineapp.pelicula.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class AmazonS3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public AmazonS3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // URL pública si el bucket tiene política pública configurada
            return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al leer el archivo");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al subir a S3");
        }
    }
}

