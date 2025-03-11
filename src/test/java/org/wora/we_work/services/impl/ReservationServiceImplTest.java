//package org.wora.we_work.services.impl;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.wora.we_work.dto.reservation.ReservationRequest;
//import org.wora.we_work.dto.reservation.ReservationResponse;
//import org.wora.we_work.entities.EspaceCoworking;
//import org.wora.we_work.entities.Paiement;
//import org.wora.we_work.entities.Reservation;
//import org.wora.we_work.entities.User;
//import org.wora.we_work.mapper.ReservationMapper;
//import org.wora.we_work.repository.EspaceCoworkingRepository;
//import org.wora.we_work.repository.PaiementRepository;
//import org.wora.we_work.repository.ReservationRepository;
//import org.wora.we_work.repository.UserRepository;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.Optional;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//class ReservationServiceImplTest {
//    @Mock private ReservationRepository reservationRepository;
//    @Mock private EspaceCoworkingRepository espaceRepository;
//    @Mock private UserRepository userRepository;
//    @Mock private PaiementRepository paiementRepository;
//    @Mock private ReservationMapper reservationMapper;
//    @InjectMocks private ReservationServiceImpl reservationService;
//
//    @Nested
//    class CreerReservationTests {
//        @Test
//        void creerReservation_Success() {
//
//            LocalDateTime dateDebut = LocalDateTime.now();
//            LocalDateTime dateFin = dateDebut.plusHours(2);
//
//            ReservationRequest request = new ReservationRequest();
//            request.setDateDebut(dateDebut);
//            request.setDateFin(dateFin);
//            request.setEspaceId(1L);
//            request.setNombrePersonnes(2);
//
//            User user = new User();
//            user.setId(1L);
//
//            EspaceCoworking espace = new EspaceCoworking();
//            espace.setId(1L);
//            espace.setCapacite(5);
//            espace.setPrixParJour(20.00);
//
//
//            Reservation savedReservation = new Reservation();
//            savedReservation.setId(1L);
//            savedReservation.setEspace(espace);
//            savedReservation.setUser(user);
//            savedReservation.setDateDebut(dateDebut);
//            savedReservation.setDateFin(dateFin);
//            savedReservation.setStatut("EN_ATTENTE");
//            savedReservation.setNombrePersonnes(request.getNombrePersonnes());
//
//            ReservationResponse mockResponse = new ReservationResponse();
//            mockResponse.setId(1L);
//
//            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//            when(espaceRepository.findById(1L)).thenReturn(Optional.of(espace));
//            when(reservationRepository.findOverlappingReservations(
//                    eq(request.getEspaceId()),
//                    eq(request.getDateDebut()),
//                    eq(request.getDateFin())
//            )).thenReturn(Collections.emptyList());
//            when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);
//            when(reservationMapper.toResponse(any(Reservation.class))).thenReturn(mockResponse);
//
//            ReservationResponse response = reservationService.creerReservation(request, 1L);
//
//            assertNotNull(response);
//            assertEquals(1L, response.getId());
//
//            verify(userRepository).findById(1L);
//            verify(espaceRepository).findById(1L);
//            verify(reservationRepository).findOverlappingReservations(
//                    eq(request.getEspaceId()),
//                    eq(request.getDateDebut()),
//                    eq(request.getDateFin())
//            );
//            verify(reservationRepository).save(any(Reservation.class));
//            verify(paiementRepository).save(argThat(paiement ->
//                    Math.abs(paiement.getMontant().doubleValue() - 1.6666666666666667) < 0.0001 &&
//                            paiement.getStatut().equals("EN_ATTENTE") &&
//                            paiement.getReservation() == savedReservation
//            ));
//            verify(reservationMapper).toResponse(savedReservation);
//        }
//
//        @Test
//        void calculerPrixTotal_VerificationCalcul() {
//            EspaceCoworking espace = new EspaceCoworking();
//            espace.setPrixParJour(20.00);
//
//            LocalDateTime dateDebut = LocalDateTime.now();
//            LocalDateTime dateFin = dateDebut.plusHours(2);
//
//            ReservationRequest request = new ReservationRequest();
//            request.setDateDebut(dateDebut);
//            request.setDateFin(dateFin);
//
//
//            double prixParHeure = 20.00 / 24;
//            double prixAttendu = prixParHeure * 2;
//
//            assertTrue(Math.abs(prixAttendu - 1.6666666666666667) < 0.0001,
//                    "Le prix calculé devrait être environ 1.67 pour 2 heures avec un prix journalier de 20.00");
//        }
//    }
//    @Nested
//    class AnnulerReservationTests {
//        @Test
//        void annulerReservation_Success() {
//            Reservation reservation = new Reservation();
//            reservation.setId(1L);
//            reservation.setDateDebut(LocalDateTime.now().plusDays(2));
//
//            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
//
//            reservationService.annulerReservation(1L);
//
//            verify(paiementRepository).deleteByReservation(reservation);
//            verify(reservationRepository).delete(reservation);
//        }
//
//        @Test
//        void annulerReservation_MoinsDe24h() {
//            Reservation reservation = new Reservation();
//            reservation.setId(1L);
//            reservation.setDateDebut(LocalDateTime.now().plusHours(12));
//
//            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
//
//            assertThrows(IllegalStateException.class,
//                    () -> reservationService.annulerReservation(1L));
//        }
//    }
//}