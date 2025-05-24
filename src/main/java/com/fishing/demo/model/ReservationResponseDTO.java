package com.fishing.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReservationResponseDTO {
    private Long id;
    private LocalDate date;
    private int persons;
    private Long locationId;
    private String username;       // înlocuit userId cu username
    private LocalDateTime createdAt;
}
