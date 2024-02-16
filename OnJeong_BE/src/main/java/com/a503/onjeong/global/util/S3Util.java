package com.a503.onjeong.global.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Util {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public String uploadFile(MultipartFile file) throws IOException {
        String S3FileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        // ObjectMetadata를 생성하여 content-type 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());

        // PutObjectRequest 생성 및 content-type 설정
        PutObjectRequest request = new PutObjectRequest(bucket, S3FileName, file.getInputStream(), metadata);
        amazonS3.putObject(request);
        return S3FileName;
    }

    public void deleteFile(String S3FileName) {

        amazonS3.deleteObject(bucket, S3FileName);
    }

    public String getFile(String S3FileName) {

        try {
            URL url = amazonS3.getUrl(bucket, S3FileName);
            return (url != null) ? url.getPath() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
