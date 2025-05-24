// src/main/java/com/fishing/demo/repository/ReservationRepository.java
package com.fishing.demo.repository;

import com.fishing.demo.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // rezervări ale unui user
    List<Reservation> findAllByUserIdOrderByDateDesc(Long userId);

    // rezervări pentru o locație pe o dată, pentru sumă
    List<Reservation> findAllByLocationIdAndDate(Long locationId, LocalDate date);

    // toate rezervările pentru o locație (owner)
    List<Reservation> findAllByLocationIdOrderByDateDesc(Long locationId);

    // nou: verifică dacă userul are deja rezervare pentru acea locație și dată
    boolean existsByLocationIdAndDateAndUserId(Long locationId, LocalDate date, Long userId);
}
