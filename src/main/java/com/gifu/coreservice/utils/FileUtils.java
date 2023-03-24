package com.gifu.coreservice.utils;

import liquibase.util.file.FilenameUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    private void createDirectoryIfNotExists(String directoryPath) throws IOException {
        if (!Files.exists(Paths.get(directoryPath))) {
            Files.createDirectories(Paths.get(directoryPath));
        }
    }

    public static String getFileExtension(String filename) {
        if(StringUtils.hasText(filename)){
            return "";
        }
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > 0) {
            return filename.substring(dotIndex + 1);
        } else {
            return "";
        }
    }


    public String storeFile(MultipartFile multipartFile, String systemPath) throws IOException {
        createDirectoryIfNotExists(systemPath);
        String extension = getFileExtension(multipartFile.getOriginalFilename());
        String newFileName = System.currentTimeMillis() + "." + extension;
        String filePath = systemPath + File.separator + newFileName;
        File path = new File(filePath);
        path.createNewFile();
        FileOutputStream output = new FileOutputStream(path);
        output.write(multipartFile.getBytes());
        output.close();
        return newFileName;
    }

}
