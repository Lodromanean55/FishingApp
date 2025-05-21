package com.fishing.demo.service;

import com.fishing.demo.exceptions.FishingLocationValidationException;
import com.fishing.demo.model.FishingLocation;
import com.fishing.demo.model.Localization;
import com.fishing.demo.model.LocalizationDTO;
import com.fishing.demo.model.FishingLocationRequestDTO;
import com.fishing.demo.model.FishingLocationResponseDTO;
import com.fishing.demo.repository.FishingLocationRepository;
import com.fishing.demo.repository.ReviewRepository;
import com.fishing.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FishingLocationService {

    private final FishingLocationRepository repo;
    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;
    private final FileStorageService fileStorage;

    public FishingLocationService(FishingLocationRepository repo,
                                  ReviewRepository reviewRepo,
                                  UserRepository userRepo,
                                  FileStorageService fileStorage) {
        this.repo = repo;
        this.reviewRepo = reviewRepo;
        this.userRepo = userRepo;
        this.fileStorage = fileStorage;
    }

    public FishingLocationResponseDTO createLocation(Authentication auth, FishingLocationRequestDTO dto) {
        String username = auth.getName();
        var owner = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User inexistent"));

        FishingLocation loc = new FishingLocation();
        loc.setName(dto.getName());
        loc.setAddress(dto.getAddress());
        loc.setDescription(dto.getDescription());
        loc.setHasBoatFishing(dto.isHasBoatFishing());
        loc.setMaxPersons(dto.getMaxPersons());
        loc.setRules(dto.getRules());
        loc.setPricePerPerson(dto.getPricePerPerson());

        loc.setLocalizations(dto.getLocalizations().stream()
                .map(d -> {
                    Localization l = new Localization();
                    l.setCity(d.getCity());
                    l.setDistanceKm(d.getDistanceKm());
                    l.setDuration(d.getDuration());
                    return l;
                })
                .collect(Collectors.toList())
        );
        loc.setFacilities(dto.getFacilities());
        loc.setSpecies(dto.getSpecies());
        loc.setNumberOfStands(dto.getNumberOfStands());
        loc.setEquipmentRental(dto.isEquipmentRental());

        loc.setOwner(owner);
        var saved = repo.save(loc);
        return mapToResponse(saved);
    }

    public List<FishingLocationResponseDTO> getAllLocations() {
        return repo.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public FishingLocationResponseDTO getLocationById(Long id) {
        var loc = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locație negăsită"));
        return mapToResponse(loc);
    }

    @Transactional
    public FishingLocationResponseDTO updateLocation(Long id, Authentication auth, FishingLocationRequestDTO dto) {
        var loc = repo.findById(id)
                .orElseThrow(() -> new FishingLocationValidationException("Locația nu există"));

        if (!loc.getOwner().getUsername().equals(auth.getName())) {
            throw new FishingLocationValidationException("Nu aveți drept de modificare");
        }

        loc.setName(dto.getName());
        loc.setAddress(dto.getAddress());
        loc.setDescription(dto.getDescription());
        loc.setHasBoatFishing(dto.isHasBoatFishing());
        loc.setMaxPersons(dto.getMaxPersons());
        loc.setRules(dto.getRules());
        loc.setPricePerPerson(dto.getPricePerPerson());
        loc.setLocalizations(dto.getLocalizations().stream()
                .map(d -> {
                    Localization l = new Localization();
                    l.setCity(d.getCity());
                    l.setDistanceKm(d.getDistanceKm());
                    l.setDuration(d.getDuration());
                    return l;
                })
                .collect(Collectors.toList())
        );
        loc.setFacilities(dto.getFacilities());
        loc.setSpecies(dto.getSpecies());
        loc.setNumberOfStands(dto.getNumberOfStands());
        loc.setEquipmentRental(dto.isEquipmentRental());

        var saved = repo.save(loc);
        return mapToResponse(saved);
    }

    @Transactional
    public FishingLocationResponseDTO uploadImages(Long id,
                                                   Authentication auth,
                                                   List<MultipartFile> files) {
        var loc = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locație negăsită"));
        if (!loc.getOwner().getUsername().equals(auth.getName())) {
            throw new FishingLocationValidationException("Nu aveți drept de modificare a imaginilor");
        }
        var paths = files.stream()
                .map(file -> fileStorage.storeFile("loc_" + id, file))
                .collect(Collectors.toList());
        loc.getImagePaths().addAll(paths);
        var saved = repo.save(loc);
        return mapToResponse(saved);
    }

    /**
     * Șterge recenziile asociate și apoi locația, într-o singură tranzacție.
     */
    @Transactional
    public void deleteLocation(Long id, Authentication auth) {
        var loc = repo.findById(id)
                .orElseThrow(() -> new FishingLocationValidationException("Locația nu există"));

        if (!loc.getOwner().getUsername().equals(auth.getName())) {
            throw new FishingLocationValidationException("Nu sunteți proprietarul");
        }

        // 1️⃣ Ștergem toate recenziile legate de această locație
        reviewRepo.deleteAllByLocationId(id);

        // 2️⃣ Ștergem locația propriu-zisă
        repo.delete(loc);
    }

    private FishingLocationResponseDTO mapToResponse(FishingLocation loc) {
        return new FishingLocationResponseDTO(
                loc.getId(),
                loc.getName(),
                loc.getAddress(),
                loc.getDescription(),
                loc.isHasBoatFishing(),
                loc.getMaxPersons(),
                loc.getRules(),
                loc.getPricePerPerson(),
                loc.getOwner().getId(),
                loc.getCreatedAt(),
                loc.getUpdatedAt(),
                loc.getImagePaths(),
                loc.getLocalizations().stream()
                        .map(l -> new LocalizationDTO(l.getCity(), l.getDistanceKm(), l.getDuration()))
                        .collect(Collectors.toList()),
                loc.getFacilities(),
                loc.getSpecies(),
                loc.getNumberOfStands(),
                loc.isEquipmentRental()
        );
    }
}
