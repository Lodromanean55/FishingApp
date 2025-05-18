package com.fishing.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReviewResponseDTO {
    private Long id;
    private int rating;
    private String comment;
    private String authorUsername;
    private LocalDateTime createdAt;
}
