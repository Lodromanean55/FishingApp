package com.fishing.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fishing_location")
@Data
public class FishingLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean hasBoatFishing;

    @Column(nullable = false)
    private int maxPersons;

    @Column(columnDefinition = "TEXT")
    private String rules;

    @Column(nullable = false)
    private BigDecimal pricePerPerson;

    // --- stocăm căile relative către fișierele încărcate ---
    @ElementCollection
    @CollectionTable(name = "location_images", joinColumns = @JoinColumn(name = "location_id"))
    @Column(name = "image_path", nullable = false)
    private List<String> imagePaths = new ArrayList<>();

    // --- celelalte câmpuri existente ---
    @ElementCollection
    @CollectionTable(name = "location_localizations", joinColumns = @JoinColumn(name = "location_id"))
    private List<Localization> localizations;

    @ElementCollection
    @CollectionTable(name = "location_facilities", joinColumns = @JoinColumn(name = "location_id"))
    @Column(name = "facility")
    private List<String> facilities;

    @ElementCollection
    @CollectionTable(name = "location_species", joinColumns = @JoinColumn(name = "location_id"))
    @Column(name = "species")
    private List<String> species;

    private int numberOfStands;
    private boolean equipmentRental;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
