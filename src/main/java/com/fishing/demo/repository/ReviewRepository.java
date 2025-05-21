package com.fishing.demo.repository;

import com.fishing.demo.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByLocationIdOrderByCreatedAtDesc(Long locationId);
    void deleteAllByLocationId(Long locationId);
}
