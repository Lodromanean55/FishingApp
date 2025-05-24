package com.fishing.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"location_id","date","user_id"}))
@Data
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Data zilei rezervate */
    @Column(nullable = false)
    private LocalDate date;

    /** Număr de persoane pentru rezervare */
    @Column(nullable = false)
    private int persons;

    /** Cine rezervă */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Pentru ce locație */
    @ManyToOne(optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private FishingLocation location;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
