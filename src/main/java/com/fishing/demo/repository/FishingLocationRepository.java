package com.fishing.demo.repository;

import com.fishing.demo.model.FishingLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FishingLocationRepository extends JpaRepository<FishingLocation, Long> {
}
