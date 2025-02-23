package com.starter.starter.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {
    private static final String UPLOAD_DIR = "C:\\Users\\Ando Niaina\\Documents\\Front-end\\public\\image";

    @GetMapping("/images/{filename}")
    public Resource getImage(@PathVariable String filename) throws Exception {
        Path filePath = Paths.get(UPLOAD_DIR, filename);
        System.out.println("Serving image: " + filePath.toString()); // Vérifie le chemin d'accès
        return new UrlResource(filePath.toUri());
    }

}
