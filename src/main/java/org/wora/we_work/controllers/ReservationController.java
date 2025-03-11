package org.wora.we_work.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wora.we_work.dto.reservation.ReservationRequest;
import org.wora.we_work.dto.reservation.ReservationResponse;
import org.wora.we_work.services.api.ReservationService;

@RestController
@RequestMapping("/api/booking")
@AllArgsConstructor
public class ReservationController {

    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest reservationRequest) {
        ReservationResponse response = reservationService.createReservation(reservationRequest);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<Page<ReservationResponse>> getAllReservations(Pageable pageable) {
        Page<ReservationResponse> reservations = reservationService.getAllReservations(pageable);
        return ResponseEntity.ok(reservations);
    }
}

