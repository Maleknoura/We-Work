package org.wora.we_work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wora.we_work.entities.Paiement;
import org.wora.we_work.entities.Reservation;

import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    void deleteByReservation(Reservation reservation);

    List<Paiement> findByReservationId(Long reservationId);
}
