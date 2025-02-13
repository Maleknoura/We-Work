package org.wora.we_work.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wora.we_work.dto.reservation.ReservationRequest;
import org.wora.we_work.dto.reservation.ReservationResponse;
import org.wora.we_work.entities.*;
import org.wora.we_work.mapper.ReservationMapper;
import org.wora.we_work.repository.*;
import org.wora.we_work.services.api.ReservationService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final EspaceCoworkingRepository espaceRepository;
    private final UserRepository userRepository;
    private final PaiementRepository paiementRepository;
    private final ReservationMapper reservationMapper;

    @Override
    @Transactional
    public ReservationResponse creerReservation(ReservationRequest request, Long userId) {
        if (request.getDateFin().isBefore(request.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        EspaceCoworking espace = espaceRepository.findById(request.getEspaceId())
                .orElseThrow(() -> new RuntimeException("Espace non trouvé"));

        List<Reservation> reservationsExistantes = reservationRepository
                .findOverlappingReservations(
                        request.getEspaceId(),
                        request.getDateDebut(),
                        request.getDateFin()
                );

        if (!reservationsExistantes.isEmpty()) {
            throw new IllegalStateException("L'espace n'est pas disponible pour ces dates");
        }

        if (request.getNombrePersonnes() > espace.getCapaciteMax()) {
            throw new IllegalArgumentException("Nombre de personnes supérieur à la capacité maximale");
        }

        BigDecimal prixTotal = calculerPrixTotal(
                espace,
                request.getDateDebut(),
                request.getDateFin()
        );

        Reservation reservation = new Reservation();
        reservation.setEspace(espace);
        reservation.setUser(user);
        reservation.setDateDebut(request.getDateDebut());
        reservation.setDateFin(request.getDateFin());
        reservation.setStatut("EN_ATTENTE");
        reservation.setPrixTotal(prixTotal);
        reservation.setNombrePersonnes(request.getNombrePersonnes());

        reservation = reservationRepository.save(reservation);

        Paiement paiement = new Paiement();
        paiement.setReservation(reservation);
        paiement.setMontant(prixTotal);
        paiement.setStatut("EN_ATTENTE");
        paiement.setDatePaiement(LocalDateTime.now());
        paiementRepository.save(paiement);

        return reservationMapper.toResponse(reservation);
    }

    private BigDecimal calculerPrixTotal(
            EspaceCoworking espace,
            LocalDateTime debut,
            LocalDateTime fin
    ) {
        long heures = ChronoUnit.HOURS.between(debut, fin);
        return espace.getPrixParHeure().multiply(BigDecimal.valueOf(heures));
    }

    @Override
    public ReservationResponse getReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        return reservationMapper.toResponse(reservation);
    }

    @Override
    public List<ReservationResponse> getReservationsUtilisateur(Long utilisateurId) {
        return reservationRepository.findByUserId(utilisateurId).stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void annulerReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (reservation.getDateDebut().minusHours(24).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Impossible d'annuler une réservation moins de 24h avant");
        }

        reservation.setStatut("ANNULEE");
        reservationRepository.save(reservation);
    }
}