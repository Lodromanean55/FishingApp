package com.fishing.demo.controller;

import com.fishing.demo.model.*;
import com.fishing.demo.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations/{locId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService svc;

    /** GET public: toate recenziile pentru o locație */
    @GetMapping
    public List<ReviewResponseDTO> list(@PathVariable Long locId) {
        return svc.getReviewsForLocation(locId);
    }

    /** POST protejat: adaugă o recenzie */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponseDTO add(@PathVariable Long locId,
                                 @Valid @RequestBody ReviewRequestDTO dto,
                                 Authentication auth) {
        return svc.addReview(locId, auth, dto);
    }
}
