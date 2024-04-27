package org.example.teamspark.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CommonsLog
@Component
public class S3Uploader {

    @Value("${s3.bucket.name}")
    private String bucketName;

    private AmazonS3 getS3Client() {
        // Create an S3 client
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.AP_NORTHEAST_1)
                .build();
    }

    public void uploadFile(String saveKeyName, File file) {
        AmazonS3 s3 = getS3Client();
        log.info("Get s3 permission");
        try {
            s3.putObject(bucketName, saveKeyName, file);
        } catch (AmazonServiceException e) {
            log.error("upload " + saveKeyName + " failed: " + e.getMessage());
        }
        log.info("upload " + saveKeyName + " success");
    }

    public List<String> getFileList(String folderFilePrefix) {
        AmazonS3 s3 = getS3Client();

        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(folderFilePrefix);
        List<String> keyList = new ArrayList<>();
        try {
            ListObjectsV2Result result = s3.listObjectsV2(request);
            List<S3ObjectSummary> objects = result.getObjectSummaries();

            keyList = objects.stream()
                    .map(S3ObjectSummary::getKey)
                    .collect(Collectors.toList());
        } catch (AmazonServiceException e) {
            log.error("Get file list with folder " + folderFilePrefix + " failed: " + e.getMessage());
        }
        return keyList;
    }

}
