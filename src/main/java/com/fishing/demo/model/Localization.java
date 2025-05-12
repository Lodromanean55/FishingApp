package com.fishing.demo.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Localization {
    private String city;
    private int distanceKm;
    private String duration;
}
