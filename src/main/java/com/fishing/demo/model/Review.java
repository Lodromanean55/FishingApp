package com.fishing.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Rating de la 1 la 5 */
    @Column(nullable = false)
    private int rating;

    /** Textul recenziei */
    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Mulțime de recenzii pentru o singură locație */
    @ManyToOne(optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private FishingLocation location;

    /** Cine a scris recenzia */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;
}
