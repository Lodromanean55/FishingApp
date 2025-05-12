package com.fishing.demo.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalizationDTO {

    @NotBlank(message = "Orașul este obligatoriu")
    private String city;

    @Min(value = 0, message = "Distanța trebuie să fie un număr pozitiv")
    private int distanceKm;

    @NotBlank(message = "Durata este obligatorie")
    private String duration;
}
