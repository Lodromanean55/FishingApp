package com.fishing.demo.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FishingLocationRequestDTO {

    @NotBlank(message = "Numele este obligatoriu")
    private String name;

    @NotBlank(message = "Adresa este obligatorie")
    private String address;

    private String description;

    private boolean hasBoatFishing;

    @Min(value = 1, message = "Numărul maxim de persoane trebuie să fie cel puțin 1")
    private int maxPersons;

    private String rules;

    @NotNull(message = "Tariful per persoană este obligatoriu")
    @DecimalMin(value = "0.0", inclusive = false, message = "Tariful trebuie să fie > 0")
    private BigDecimal pricePerPerson;

    // --- noile câmpuri ---
    @NotEmpty(message = "Trebuie să specifici cel puțin o localizare")
    private List<LocalizationDTO> localizations;

    @NotEmpty(message = "Trebuie să specifici cel puțin o facilitate")
    private List<@NotBlank(message = "Facilitatea nu poate fi goală") String> facilities;

    @NotEmpty(message = "Trebuie să specifici cel puțin o specie")
    private List<@NotBlank(message = "Specia nu poate fi goală") String> species;

    @Min(value = 0, message = "Numărul de standuri nu poate fi negativ")
    private int numberOfStands;

    private boolean equipmentRental;
}
