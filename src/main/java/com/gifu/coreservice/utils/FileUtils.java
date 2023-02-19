package com.gifu.coreservice.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class FileUtils {
    public File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }
    }

    public String storeFile(MultipartFile multipartFile, String systemPath) throws IOException {
        String filePath = systemPath + multipartFile.getOriginalFilename();
        File path = new File(filePath);
        path.createNewFile();
        FileOutputStream output = new FileOutputStream(path);
        output.write(multipartFile.getBytes());
        output.close();
        return filePath;
    }

}
