package org.example.teamspark.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CommonsLog
@Component
@Profile("aws")
public class S3FileUploadUtil implements FileUploadUtil {

    @Value("${s3.bucket.name}")
    private String bucketName;

    private AmazonS3 getS3Client() {
        // Create an S3 client
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.AP_NORTHEAST_1)
                .build();
    }

    private static File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        return file;
    }

    public void uploadFile(MultipartFile file, String fileName) throws IOException {
        File convertedFile = convertMultipartToFile(file);
        uploadToS3(fileName, convertedFile);

        convertedFile.delete();
        log.info("Delete temp file");
    }

    private void uploadToS3(String saveKeyName, File file) {
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
