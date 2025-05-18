package com.fishing.demo.service;

import com.fishing.demo.exceptions.FishingLocationValidationException;
import com.fishing.demo.model.*;
import com.fishing.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final FishingLocationRepository locRepo;
    private final UserRepository userRepo;

    public List<ReviewResponseDTO> getReviewsForLocation(Long locId) {
        // verificăm existența locației
        locRepo.findById(locId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locație negăsită"));
        return reviewRepo.findAllByLocationIdOrderByCreatedAtDesc(locId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ReviewResponseDTO addReview(Long locId,
                                       Authentication auth,
                                       ReviewRequestDTO dto) {
        var loc = locRepo.findById(locId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locație negăsită"));
        var username = auth.getName();
        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User inexistent"));

        Review rev = new Review();
        rev.setRating(dto.getRating());
        rev.setComment(dto.getComment());
        rev.setLocation(loc);
        rev.setAuthor(user);

        Review saved = reviewRepo.save(rev);
        return mapToDto(saved);
    }

    private ReviewResponseDTO mapToDto(Review r) {
        return new ReviewResponseDTO(
                r.getId(),
                r.getRating(),
                r.getComment(),
                r.getAuthor().getUsername(),
                r.getCreatedAt()
        );
    }
}
