package org.wora.we_work.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wora.we_work.enums.Status;
import org.wora.we_work.entities.EspaceCoworking;
import org.wora.we_work.entities.Reservation;
import org.wora.we_work.exception.ResourceUnavailableException;
import org.wora.we_work.repository.ReservationRepository;
import org.wora.we_work.services.api.ValidationService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final ReservationRepository reservationRepository;

    @Override
    public void verifySpaceAvailability(EspaceCoworking espace, LocalDateTime dateDebut, LocalDateTime dateFin) {
        final List<Reservation> reservationsExistantes = reservationRepository
                .findOverlappingReservations(espace.getId(), dateDebut, dateFin);

        if (!reservationsExistantes.isEmpty()) {
            throw new ResourceUnavailableException("L'espace n'est pas disponible pour les dates sélectionnées");
        }

        if (!espace.isActive()) {
            throw new ResourceUnavailableException("Cet espace de coworking n'est pas disponible actuellement");
        }
    }

    @Override
    public void verifyCancellationPossibility(Reservation reservation) {
        if (reservation.getStatut() == Status.ANNULEE) {
            throw new IllegalStateException("Cette réservation est déjà annulée");
        }
        if (reservation.getStatut() == Status.TERMINEE) {
            throw new IllegalStateException("Impossible d'annuler une réservation terminée");
        }

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime debutReservation = reservation.getDateDebut().toLocalDate().atStartOfDay();

        if (now.isAfter(debutReservation)) {
            throw new IllegalStateException("Impossible d'annuler une réservation déjà commencée");
        }
    }
}
