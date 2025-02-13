package org.wora.we_work.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.wora.we_work.dto.reservation.ReservationRequest;
import org.wora.we_work.dto.reservation.ReservationResponse;
import org.wora.we_work.services.api.ReservationService;
import org.wora.we_work.services.api.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('RESERVATION_CREATE')")
    public ResponseEntity<ReservationResponse> creerReservation(
            @RequestBody @Valid ReservationRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long utilisateurId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(reservationService.creerReservation(request, utilisateurId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('RESERVATION_READ')")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservation(id));
    }

    @GetMapping("/mes-reservations")
    @PreAuthorize("hasAuthority('RESERVATION_READ')")
    public ResponseEntity<List<ReservationResponse>> getMesReservations(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        Long utilisateurId = userService.getUserIdByEmail(email);
        return ResponseEntity.ok(reservationService.getReservationsUtilisateur(utilisateurId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('RESERVATION_DELETE')")
    public ResponseEntity<Void> annulerReservation(@PathVariable Long id) {
        reservationService.annulerReservation(id);
        return ResponseEntity.noContent().build();
    }
}
