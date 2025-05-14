package com.fishing.demo.service;

import com.fishing.demo.exceptions.FishingLocationValidationException;
import com.fishing.demo.model.*;
import com.fishing.demo.repository.FishingLocationRepository;
import com.fishing.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FishingLocationService {

    private final FishingLocationRepository repo;
    private final UserRepository userRepo;
    private final FileStorageService fileStorage;  // nou, pentru upload de imagini

    public FishingLocationService(FishingLocationRepository repo,
                                  UserRepository userRepo,
                                  FileStorageService fileStorage) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.fileStorage = fileStorage;
    }

    public FishingLocationResponseDTO createLocation(Authentication auth, FishingLocationRequestDTO dto) {
        String username = auth.getName();
        User owner = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User inexistent"));

        FishingLocation loc = new FishingLocation();
        loc.setName(dto.getName());
        loc.setAddress(dto.getAddress());
        loc.setDescription(dto.getDescription());
        loc.setHasBoatFishing(dto.isHasBoatFishing());
        loc.setMaxPersons(dto.getMaxPersons());
        loc.setRules(dto.getRules());
        loc.setPricePerPerson(dto.getPricePerPerson());

        // mapăm noile câmpuri
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

        // lista de imagini rămâne goală la creare
        // loc.getImagePaths() e deja inițializată cu ArrayList în entity

        loc.setOwner(owner);
        FishingLocation saved = repo.save(loc);
        return mapToResponse(saved);
    }

    public List<FishingLocationResponseDTO> getAllLocations() {
        return repo.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public FishingLocationResponseDTO getLocationById(Long id) {
        FishingLocation loc = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locație negăsită"));
        return mapToResponse(loc);
    }

    @Transactional
    public FishingLocationResponseDTO updateLocation(Long id, Authentication auth, FishingLocationRequestDTO dto) {
        FishingLocation loc = repo.findById(id)
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

        // remapăm noile câmpuri
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

        FishingLocation saved = repo.save(loc);
        return mapToResponse(saved);
    }

    /**
     * Încarcă una sau mai multe imagini pentru o locație existentă.
     */
    @Transactional
    public FishingLocationResponseDTO uploadImages(Long id,
                                                   Authentication auth,
                                                   List<MultipartFile> files) {
        FishingLocation loc = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locație negăsită"));

        if (!loc.getOwner().getUsername().equals(auth.getName())) {
            throw new FishingLocationValidationException("Nu aveți drept de modificare a imaginilor");
        }

        // pentru fiecare fișier, salvează-l și colectează calea
        List<String> paths = files.stream()
                .map(file -> fileStorage.storeFile("loc_" + id, file))
                .collect(Collectors.toList());

        // adaugă noile path-uri în lista existentă
        loc.getImagePaths().addAll(paths);

        FishingLocation saved = repo.save(loc);
        return mapToResponse(saved);
    }

    public void deleteLocation(Long id, Authentication auth) {
        FishingLocation loc = repo.findById(id)
                .orElseThrow(() -> new FishingLocationValidationException("Locația nu există"));

        if (!loc.getOwner().getUsername().equals(auth.getName())) {
            throw new FishingLocationValidationException("Nu sunteți proprietarul");
        }
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
                // nou: lista de imagini
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
