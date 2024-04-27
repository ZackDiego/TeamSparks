package org.example.teamspark.service;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@CommonsLog
public class FileUploadService {

    @Autowired
    private S3Uploader s3Uploader;

    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return ""; // No file extension found
        }
        return fileName.substring(lastDotIndex + 1);
    }

    private static File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        return file;
    }

    public void saveMultipartFile(MultipartFile file, String filePath) throws IOException {
        File convertedFile = convertMultipartToFile(file);
        s3Uploader.uploadFile(filePath, convertedFile);

        convertedFile.delete();
        log.info("Delete temp file");
    }
}
