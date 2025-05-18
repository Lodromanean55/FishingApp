package com.fishing.demo.model;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class ReviewRequestDTO {
    @Min(value = 1, message = "Rating minim 1")
    @Max(value = 5, message = "Rating maxim 5")
    private int rating;

    @NotBlank(message = "Comentariul nu poate fi gol")
    @Size(min = 5, message = "Comentariul este prea scurt")
    private String comment;
}
