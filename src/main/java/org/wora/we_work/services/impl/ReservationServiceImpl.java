package org.wora.we_work.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wora.we_work.enums.Status;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.dto.reservation.ReservationRequest;
import org.wora.we_work.dto.reservation.ReservationResponse;
import org.wora.we_work.entities.*;
import org.wora.we_work.exception.ResourceNotFoundException;
import org.wora.we_work.mapper.ReservationMapper;
import org.wora.we_work.repository.*;
import org.wora.we_work.services.api.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final EspaceCoworkingService espaceCoworkingService;
    private final EquipementService equipementService;
    private final ValidationService validationService;
    private  final UserService userService;

    @Override
    public ReservationResponse createReservation(ReservationRequest reservationRequest) {
        User user = userService.getCurrentUser();

        EspaceCoworkingResponseDTO espaceDTO = espaceCoworkingService.getById(reservationRequest.espaceId());
        EspaceCoworking espace = reservationMapper.espaceDtoToEntity(espaceDTO);

        validationService.verifySpaceAvailability(espace, reservationRequest.dateDebut(), reservationRequest.dateFin());

        equipementService.verifierDisponibiliteEquipements(reservationRequest.equipementIds());

        Reservation reservation = reservationMapper.toEntity(reservationRequest);
        reservation.setEspace(espace);
        reservation.setUser(user);

        final BigDecimal prixTotalEquipements = equipementService.calculerPrixEquipements(reservationRequest.equipementIds(), reservationRequest.nombrePersonnes());
        final BigDecimal prixBase = espaceCoworkingService.calculerPrixBase(espace, reservationRequest.nombrePersonnes(), reservationRequest.dateDebut().toLocalDate(), reservationRequest.dateFin().toLocalDate());

        reservation.setPrixTotal(prixBase.add(prixTotalEquipements));

        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }


    @Transactional(readOnly = true)
    @Override
    public ReservationResponse getReservationById(Long id) {
        final Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID: " + id));
        return reservationMapper.toResponse(reservation);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ReservationResponse> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable).map(reservationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ReservationResponse> getReservationsByUser(Long userId, Pageable pageable) {
        return reservationRepository.findByUserId(userId, pageable).map(reservationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ReservationResponse> getReservationsByEspace(Long espaceId, Pageable pageable) {
        return reservationRepository.findByEspaceId(espaceId, pageable).map(reservationMapper::toResponse);
    }

    @Override
    public ReservationResponse updateReservation(Long id, ReservationRequest reservationRequest) {
        final Reservation existingReservation = reservationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID: " + id));

        if (!existingReservation.getEspace().getId().equals(reservationRequest.espaceId())) {
            final EspaceCoworkingResponseDTO espaceCoworkingDTO = espaceCoworkingService.getById(reservationRequest.espaceId());
            final EspaceCoworking newEspace = reservationMapper.espaceDtoToEntity(espaceCoworkingDTO);

            validationService.verifySpaceAvailability(newEspace, reservationRequest.dateDebut(), reservationRequest.dateFin());
            existingReservation.setEspace(newEspace);
        } else if (!existingReservation.getDateDebut().equals(reservationRequest.dateDebut()) || !existingReservation.getDateFin().equals(reservationRequest.dateFin())) {
            validationService.verifySpaceAvailability(existingReservation.getEspace(), reservationRequest.dateDebut(), reservationRequest.dateFin());
        }

        reservationMapper.updateEntityFromDto(reservationRequest, existingReservation);

        final BigDecimal prixTotalEquipements = equipementService.calculerPrixEquipements(reservationRequest.equipementIds(), reservationRequest.nombrePersonnes());

        final BigDecimal prixBase = espaceCoworkingService.calculerPrixBase(existingReservation.getEspace(), reservationRequest.nombrePersonnes(), reservationRequest.dateDebut().toLocalDate(), reservationRequest.dateFin().toLocalDate());

        existingReservation.setPrixTotal(prixBase.add(prixTotalEquipements));

        return reservationMapper.toResponse(reservationRepository.save(existingReservation));
    }

    @Override
    public ReservationResponse annulerReservation(Long id) {
        final Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID: " + id));

        validationService.verifyCancellationPossibility(reservation);

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime debutReservation = reservation.getDateDebut().toLocalDate().atStartOfDay();

        if (now.plusHours(24).isAfter(debutReservation)) {
            reservation.setFraisAnnulation(reservation.getPrixTotal().multiply(new BigDecimal("0.5")));
        }

        reservation.setStatut(Status.ANNULEE);
        reservation.setDateAnnulation(LocalDateTime.now());

        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }
    @Override
    public List<ReservationResponse> getReservationsForUser(Long userId) {
        List<Reservation> reservations = reservationRepository.findByEspaceCoworkingUserId(userId);

        return reservations.stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }


}