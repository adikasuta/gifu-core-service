package com.gifu.coreservice.controller.publicapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;

@Slf4j
@RestController
@RequestMapping(path = "api/public/assets")
public class FileController {
    @Value("${picture.path}")
    private String pictureBasePath;

    @GetMapping("/images")
    public ResponseEntity<Resource> downloadImages(@RequestParam("filename") String filename) throws IOException {
        Resource fileResource = new FileSystemResource(pictureBasePath + File.separator + filename);

        if (fileResource.exists()) {
            String contentType = URLConnection.guessContentTypeFromName(fileResource.getFilename());
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileResource.getFilename());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(fileResource.contentLength())
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(fileResource);
        } else {
            throw new FileNotFoundException("File not found at path: " + filename);
        }
    }
}
