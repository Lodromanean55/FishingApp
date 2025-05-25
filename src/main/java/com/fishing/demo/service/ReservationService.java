package com.fishing.demo.service;

import com.fishing.demo.exceptions.ReservationValidationException;
import com.fishing.demo.model.Reservation;
import com.fishing.demo.model.ReservationRequestDTO;
import com.fishing.demo.model.ReservationResponseDTO;
import com.fishing.demo.model.User;
import com.fishing.demo.repository.FishingLocationRepository;
import com.fishing.demo.repository.ReservationRepository;
import com.fishing.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository     repo;
    private final UserRepository            userRepo;
    private final FishingLocationRepository locRepo;

    public ReservationService(ReservationRepository repo,
                              UserRepository userRepo,
                              FishingLocationRepository locRepo) {
        this.repo     = repo;
        this.userRepo = userRepo;
        this.locRepo  = locRepo;
    }

    @Transactional
    public ReservationResponseDTO createReservation(String username,
                                                    Long locationId,
                                                    ReservationRequestDTO dto) {

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ReservationValidationException("User inexistent"));

        var loc = locRepo.findById(locationId)
                .orElseThrow(() -> new ReservationValidationException("Locație inexistentă"));

        if (repo.existsByLocationIdAndDateAndUserId(locationId, dto.getDate(), user.getId())) {
            throw new ReservationValidationException(
                    "Ai deja o rezervare pentru locația \"" +
                            loc.getName() +
                            "\" la data de " + dto.getDate()
            );
        }

        int already = repo.findAllByLocationIdAndDate(locationId, dto.getDate())
                .stream()
                .mapToInt(Reservation::getPersons)
                .sum();

        if (already + dto.getPersons() > loc.getMaxPersons()) {
            throw new ReservationValidationException(
                    "Balta este full în data de " + dto.getDate());
        }

        Reservation r = new Reservation();
        r.setUser(user);
        r.setLocation(loc);
        r.setDate(dto.getDate());
        r.setPersons(dto.getPersons());

        var saved = repo.save(r);
        return new ReservationResponseDTO(
                saved.getId(),
                saved.getDate(),
                saved.getPersons(),
                saved.getLocation().getId(),
                saved.getLocation().getName(),   // populăm numele
                saved.getUser().getUsername(),
                saved.getCreatedAt()
        );
    }

    public List<ReservationResponseDTO> listMyReservations(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ReservationValidationException("User inexistent"));

        return repo.findAllByUserIdOrderByDateDesc(user.getId())
                .stream()
                .map(r -> new ReservationResponseDTO(
                        r.getId(),
                        r.getDate(),
                        r.getPersons(),
                        r.getLocation().getId(),
                        r.getLocation().getName(),   // şi aici
                        r.getUser().getUsername(),
                        r.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public List<ReservationResponseDTO> listReservationsForLocation(String username, Long locId) {
        var loc = locRepo.findById(locId)
                .orElseThrow(() -> new ReservationValidationException("Locație inexistentă"));
        if (!loc.getOwner().getUsername().equals(username)) {
            throw new ReservationValidationException("Nu ești proprietarul acestei locații");
        }

        return repo.findAllByLocationIdOrderByDateDesc(locId)
                .stream()
                .map(r -> new ReservationResponseDTO(
                        r.getId(),
                        r.getDate(),
                        r.getPersons(),
                        r.getLocation().getId(),
                        r.getLocation().getName(),   // şi aici
                        r.getUser().getUsername(),
                        r.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public List<ReservationResponseDTO> listReservationsForOwner(String username) {
        userRepo.findByUsername(username)
                .orElseThrow(() -> new ReservationValidationException("User inexistent"));
        return repo.findAllByOwnerUsername(username);
    }

    @Transactional
    public void deleteReservation(String username, Long reservationId) {
        var reservation = repo.findById(reservationId)
                .orElseThrow(() -> new ReservationValidationException("Rezervare inexistentă"));

        if (!reservation.getUser().getUsername().equals(username)) {
            throw new ReservationValidationException("Nu poți șterge această rezervare");
        }

        repo.delete(reservation);
    }
}
