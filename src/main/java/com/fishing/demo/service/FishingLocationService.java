package com.fishing.demo.service;

import com.fishing.demo.exceptions.FishingLocationValidationException;
import com.fishing.demo.model.*;
import com.fishing.demo.repository.FishingLocationRepository;
import com.fishing.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class FishingLocationService {

    private final FishingLocationRepository repo;
    private final UserRepository userRepo;

    public FishingLocationService(FishingLocationRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
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

        // ─────────── Maparea câmpurilor noi ───────────
        loc.setLocalizations(
                dto.getLocalizations().stream()
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
        // ────────────────────────────────────────────────

        loc.setOwner(owner);
        // createdAt/updatedAt le gestionează @CreationTimestamp/@UpdateTimestamp

        FishingLocation saved = repo.save(loc);
        return mapToResponse(saved);
    }

    public java.util.List<FishingLocationResponseDTO> getAllLocations() {
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

        // ─────────── Maparea câmpurilor noi ───────────
        loc.setLocalizations(
                dto.getLocalizations().stream()
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
        // ────────────────────────────────────────────────

        // updatedAt e gestionat de @UpdateTimestamp
        return mapToResponse(loc);
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
                // mapăm colecțiile pentru răspuns
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
