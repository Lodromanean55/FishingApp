package com.fishing.demo.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationRequestDTO {
    @NotNull(message = "Data rezervării este obligatorie")
    @FutureOrPresent(message = "Nu poți rezerva pentru o zi trecută")
    private LocalDate date;

    @Min(value = 1, message = "Trebuie să rezervi cel puțin o persoană")
    private int persons;
}
