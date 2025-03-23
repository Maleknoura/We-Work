package org.wora.we_work.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wora.we_work.entities.EspaceCoworking;
import org.wora.we_work.entities.Reservation;
import org.wora.we_work.enums.Status;
import org.wora.we_work.exception.ResourceUnavailableException;
import org.wora.we_work.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ValidationServiceImpl validationService;

    private EspaceCoworking espace;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Reservation existingReservation;

    @BeforeEach
    void setUp() {
        espace = new EspaceCoworking();
        espace.setId(1L);
        espace.setNom("Espace Test");
        espace.setActive(true);

        dateDebut = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0);
        dateFin = dateDebut.plusHours(4);

        existingReservation = new Reservation();
        existingReservation.setId(1L);
        existingReservation.setEspace(espace);
        existingReservation.setDateDebut(dateDebut);
        existingReservation.setDateFin(dateFin);
    }

    @Nested
    @DisplayName("Tests pour verifySpaceAvailability")
    class VerifySpaceAvailabilityTests {

        @Test
        @DisplayName("Devrait valider une réservation lorsque l'espace est disponible")
        void shouldValidateWhenSpaceIsAvailable() {

            when(reservationRepository.findOverlappingReservations(any(), any(), any()))
                    .thenReturn(Collections.emptyList());

            LocalDateTime newDateDebut = dateDebut.plusDays(1);
            LocalDateTime newDateFin = dateFin.plusDays(1);

            assertDoesNotThrow(() ->
                    validationService.verifySpaceAvailability(espace, newDateDebut, newDateFin)
            );
        }

        @Test
        @DisplayName("Devrait rejeter une réservation lorsque l'espace n'est pas disponible")
        void shouldRejectWhenSpaceIsNotAvailable() {

            when(reservationRepository.findOverlappingReservations(1L, dateDebut, dateFin))
                    .thenReturn(List.of(existingReservation));


            ResourceUnavailableException exception = assertThrows(
                    ResourceUnavailableException.class,
                    () -> validationService.verifySpaceAvailability(espace, dateDebut, dateFin)
            );


            assertTrue(exception.getMessage().contains("n'est pas disponible"));
        }

        @Test
        @DisplayName("Devrait rejeter une réservation avec un espace inactif")
        void shouldRejectWhenSpaceIsInactive() {

            espace.setActive(false);
            when(reservationRepository.findOverlappingReservations(any(), any(), any()))
                    .thenReturn(Collections.emptyList());


            ResourceUnavailableException exception = assertThrows(
                    ResourceUnavailableException.class,
                    () -> validationService.verifySpaceAvailability(espace, dateDebut, dateFin)
            );

            assertTrue(exception.getMessage().contains("n'est pas disponible actuellement"));
        }

    }

    @Nested
    @DisplayName("Tests pour des validations complexes")
    class ComplexValidationTests {

        @Test
        @DisplayName("Devrait rejeter une réservation qui chevauche partiellement une réservation existante (début)")
        void shouldRejectWhenPartiallyOverlappingStart() {

            when(reservationRepository.findOverlappingReservations(any(), any(), any()))
                    .thenReturn(List.of(existingReservation));

            LocalDateTime newDateDebut = dateDebut.minusHours(1);
            LocalDateTime newDateFin = dateDebut.plusHours(1);

            ResourceUnavailableException exception = assertThrows(
                    ResourceUnavailableException.class,
                    () -> validationService.verifySpaceAvailability(espace, newDateDebut, newDateFin)
            );

            assertTrue(exception.getMessage().contains("n'est pas disponible"));
        }

        @Test
        @DisplayName("Devrait rejeter une réservation qui chevauche partiellement une réservation existante (fin)")
        void shouldRejectWhenPartiallyOverlappingEnd() {

            when(reservationRepository.findOverlappingReservations(any(), any(), any()))
                    .thenReturn(List.of(existingReservation));

            LocalDateTime newDateDebut = dateFin.minusHours(1);
            LocalDateTime newDateFin = dateFin.plusHours(1);

            ResourceUnavailableException exception = assertThrows(
                    ResourceUnavailableException.class,
                    () -> validationService.verifySpaceAvailability(espace, newDateDebut, newDateFin)
            );

            assertTrue(exception.getMessage().contains("n'est pas disponible"));
        }

        @Test
        @DisplayName("Devrait rejeter une réservation qui englobe complètement une réservation existante")
        void shouldRejectWhenCompletelyOverlapping() {

            when(reservationRepository.findOverlappingReservations(any(), any(), any()))
                    .thenReturn(List.of(existingReservation));

            LocalDateTime newDateDebut = dateDebut.minusHours(1);
            LocalDateTime newDateFin = dateFin.plusHours(1);

            ResourceUnavailableException exception = assertThrows(
                    ResourceUnavailableException.class,
                    () -> validationService.verifySpaceAvailability(espace, newDateDebut, newDateFin)
            );

            assertTrue(exception.getMessage().contains("n'est pas disponible"));
        }

        @Test
        @DisplayName("Devrait rejeter une réservation entièrement incluse dans une réservation existante")
        void shouldRejectWhenEntirelyWithinExisting() {

            when(reservationRepository.findOverlappingReservations(any(), any(), any()))
                    .thenReturn(List.of(existingReservation));

            LocalDateTime newDateDebut = dateDebut.plusHours(1);
            LocalDateTime newDateFin = dateFin.minusHours(1);

            ResourceUnavailableException exception = assertThrows(
                    ResourceUnavailableException.class,
                    () -> validationService.verifySpaceAvailability(espace, newDateDebut, newDateFin)
            );

            assertTrue(exception.getMessage().contains("n'est pas disponible"));
        }
    }

    @Nested
    @DisplayName("Tests pour verifyCancellationPossibility")
    class VerifyCancellationPossibilityTests {

        @Test
        @DisplayName("Devrait rejeter l'annulation d'une réservation déjà annulée")
        void shouldRejectCancellationOfAlreadyCancelledReservation() {

            existingReservation.setStatut(Status.ANNULEE);

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> validationService.verifyCancellationPossibility(existingReservation)
            );

            assertTrue(exception.getMessage().contains("déjà annulée"));
        }

        @Test
        @DisplayName("Devrait rejeter l'annulation d'une réservation terminée")
        void shouldRejectCancellationOfCompletedReservation() {
            existingReservation.setStatut(Status.TERMINEE);

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> validationService.verifyCancellationPossibility(existingReservation)
            );

            assertTrue(exception.getMessage().contains("terminée"));
        }

        @Test
        @DisplayName("Devrait rejeter l'annulation d'une réservation déjà commencée")
        void shouldRejectCancellationOfStartedReservation() {
            existingReservation.setDateDebut(LocalDateTime.now().minusHours(1));
            existingReservation.setStatut(Status.CONFIRMEE);

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> validationService.verifyCancellationPossibility(existingReservation)
            );

            assertTrue(exception.getMessage().contains("déjà commencée"));
        }

        @Test
        @DisplayName("Devrait valider l'annulation d'une réservation valide")
        void shouldValidateCancellationOfValidReservation() {

            existingReservation.setDateDebut(LocalDateTime.now().plusDays(1));
            existingReservation.setStatut(Status.CONFIRMEE);


            assertDoesNotThrow(() ->
                    validationService.verifyCancellationPossibility(existingReservation)
            );
        }
    }
}