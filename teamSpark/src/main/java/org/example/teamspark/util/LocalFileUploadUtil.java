package org.example.teamspark.util;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@CommonsLog
@Component
@Profile("local")
public class LocalFileUploadUtil implements FileUploadUtil {
    public void uploadFile(MultipartFile multipartFile, String fileName) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
    }
}