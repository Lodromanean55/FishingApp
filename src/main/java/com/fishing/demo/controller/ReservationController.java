// src/main/java/com/fishing/demo/controller/ReservationController.java
package com.fishing.demo.controller;

import com.fishing.demo.model.*;
import com.fishing.demo.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService svc;

    @PostMapping("/locations/{locId}/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponseDTO book(
            @PathVariable Long locId,
            @Valid @RequestBody ReservationRequestDTO dto,
            Authentication auth
    ) {
        return svc.createReservation(auth.getName(), locId, dto);
    }

    @GetMapping("/reservations/me")
    public List<ReservationResponseDTO> myBookings(Authentication auth) {
        return svc.listMyReservations(auth.getName());
    }

    @GetMapping("/locations/{locId}/reservations")
    public List<ReservationResponseDTO> bookingsForLocation(
            @PathVariable Long locId,
            Authentication auth
    ) {
        return svc.listReservationsForLocation(auth.getName(), locId);
    }

    @DeleteMapping("/reservations/{resId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservation(
            @PathVariable Long resId,
            Authentication auth
    ) {
        svc.deleteReservation(auth.getName(), resId);
    }
}
