package com.fishing.demo.controller;

import com.fishing.demo.model.FishingLocation;
import com.fishing.demo.service.FishingLocationService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class FishingLocationController {

    FishingLocationService fishingLocationService;

    @GetMapping("/locations")
    public List<FishingLocation> getFishingLocation() {
        return fishingLocationService.getAll();
    }

    @PostMapping("/create")
    public void createFishingLocation(Authentication auth,
                                      @RequestBody FishingLocation fishingLocation) {
        fishingLocationService.createFishingLocation(auth, fishingLocation);
    }
}
