package com.fishing.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.upload.dir}")
    private String baseUploadDir;

    @Override
    public String storeFile(String folder, MultipartFile file) {
        // normalizăm numele fișierului
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            // construim calea uploads/{folder}/
            Path uploadPath = Paths.get(baseUploadDir).resolve(folder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            // scriem fișierul
            Path target = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            // întoarcem calea relativă pentru a o salva în DB
            return baseUploadDir + "/" + folder + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Eroare la salvarea fișierului " + filename, e);
        }
    }
}
