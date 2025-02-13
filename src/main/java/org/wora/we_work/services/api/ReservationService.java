package org.wora.we_work.services.api;

import org.wora.we_work.dto.reservation.ReservationRequest;
import org.wora.we_work.dto.reservation.ReservationResponse;

import java.util.List;


public interface ReservationService {
    ReservationResponse creerReservation(ReservationRequest request, Long utilisateurId);
    ReservationResponse getReservation(Long id);
    List<ReservationResponse> getReservationsUtilisateur(Long utilisateurId);
    void annulerReservation(Long id);
}
