package com.fishing.demo.service;

import com.fishing.demo.model.FishingLocation;
import com.fishing.demo.model.User;
import com.fishing.demo.repository.FishingLocationRepository;
import com.fishing.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class FishingLocationService {

    FishingLocationRepository fishingLocationRepository;
    UserRepository userRepository;

    public List<FishingLocation> getAll() {
        return fishingLocationRepository.findAll();
    }

    public void createFishingLocation(Authentication auth, FishingLocation fishingLocation) {
        // 1. Încarcă user-ul curent
        User owner = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + auth.getName()));

        // 2. Mapare câmp cu câmp pe o entitate nouă
        FishingLocation location = new FishingLocation();
        location.setName(fishingLocation.getName());
        location.setAddress(fishingLocation.getAddress());
        location.setDescription(fishingLocation.getDescription());
        location.setHasBoatFishing(fishingLocation.isHasBoatFishing());
        location.setMaxPersons(fishingLocation.getMaxPersons());
        location.setRules(fishingLocation.getRules());
        location.setPricePerPerson(fishingLocation.getPricePerPerson());

        // 3. Leagă owner-ul și pune datele de audit
        location.setOwner(owner);
        location.setCreatedAt(LocalDateTime.now());

        // 4. Salvează și returnează
        fishingLocationRepository.save(location);
    }
}
