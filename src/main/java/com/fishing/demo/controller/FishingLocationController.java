package com.fishing.demo.controller;

import com.fishing.demo.model.FishingLocationRequestDTO;
import com.fishing.demo.model.FishingLocationResponseDTO;
import com.fishing.demo.service.FishingLocationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@AllArgsConstructor
public class FishingLocationController {

    private final FishingLocationService service;

    @GetMapping
    public List<FishingLocationResponseDTO> listAll() {
        return service.getAllLocations();
    }

    @GetMapping("/{id}")
    public FishingLocationResponseDTO getById(@PathVariable Long id) {
        return service.getLocationById(id);
    }

    @PostMapping
    public FishingLocationResponseDTO create(@Valid @RequestBody FishingLocationRequestDTO dto,
                                             Authentication auth) {
        return service.createLocation(auth, dto);
    }

    @PutMapping("/{id}")
    public FishingLocationResponseDTO update(@PathVariable Long id,
                                             @Valid @RequestBody FishingLocationRequestDTO dto,
                                             Authentication auth) {
        return service.updateLocation(id, auth, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication auth) {
        service.deleteLocation(id, auth);
    }

    // ——— nou: upload imagini ———
    @PostMapping(value = "/{id}/images", consumes = "multipart/form-data")
    public FishingLocationResponseDTO uploadImages(
            @PathVariable Long id,
            @RequestPart("files") List<MultipartFile> files,
            Authentication auth
    ) {
        return service.uploadImages(id, auth, files);
    }
}
