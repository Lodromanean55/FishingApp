package com.fishing.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class FishingLocationResponseDTO {

    private Long id;
    private String name;
    private String address;
    private String description;
    private boolean hasBoatFishing;
    private int maxPersons;
    private String rules;
    private BigDecimal pricePerPerson;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- noile câmpuri ---
    private List<LocalizationDTO> localizations;
    private List<String> facilities;
    private List<String> species;
    private int numberOfStands;
    private boolean equipmentRental;
}
