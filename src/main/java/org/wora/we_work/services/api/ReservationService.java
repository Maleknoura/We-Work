package org.wora.we_work.services.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.wora.we_work.dto.reservation.ReservationRequest;
import org.wora.we_work.dto.reservation.ReservationResponse;

import java.util.List;


public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest reservationRequest);

    ReservationResponse getReservationById(Long id);

    Page<ReservationResponse> getAllReservations(Pageable pageable);

    Page<ReservationResponse> getReservationsByUser(Long userId, Pageable pageable);

    Page<ReservationResponse> getReservationsByEspace(Long espaceId, Pageable pageable);

    ReservationResponse updateReservation(Long id, ReservationRequest reservationRequest);

    ReservationResponse annulerReservation(Long id);
    List<ReservationResponse> getReservationsForUser(Long userId);
}



