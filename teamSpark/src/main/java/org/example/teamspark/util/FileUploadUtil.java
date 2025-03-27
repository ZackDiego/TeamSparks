package org.example.teamspark.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadUtil {
    void uploadFile(MultipartFile file, String fileName) throws IOException;

    default String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return ""; // No file extension found
        }
        return fileName.substring(lastDotIndex + 1);
    }
}
