package com.fishing.demo.repository;

import com.fishing.demo.model.Reservation;
import com.fishing.demo.model.ReservationResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByUserIdOrderByDateDesc(Long userId);

    List<Reservation> findAllByLocationIdAndDate(Long locationId, LocalDate date);

    List<Reservation> findAllByLocationIdOrderByDateDesc(Long locationId);

    boolean existsByLocationIdAndDateAndUserId(Long locationId, LocalDate date, Long userId);

    /**
     * Returnează toate rezervările făcute pe locațiile unui owner,
     * inclusiv numele fiecărei locații.
     */
    @Query("""
      select new com.fishing.demo.model.ReservationResponseDTO(
        r.id,
        r.date,
        r.persons,
        r.location.id,
        r.location.name,
        r.user.username,
        r.createdAt
      )
      from Reservation r
      where r.location.owner.username = :username
      order by r.date desc
    """)
    List<ReservationResponseDTO> findAllByOwnerUsername(@Param("username") String username);
}
