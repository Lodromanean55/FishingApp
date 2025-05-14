package com.fishing.demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Salvează fişierul în subdirectorul uploads/{folder}/
     * @param folder numele folderului (ex: "loc_13")
     * @param file conţinutul fişierului multipart
     * @return path-ul relativ (ex: "uploads/loc_13/poza1.jpg")
     */
    String storeFile(String folder, MultipartFile file);
}
