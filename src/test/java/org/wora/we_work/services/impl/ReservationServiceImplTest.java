package org.wora.we_work.services.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.dto.reservation.ReservationRequest;
import org.wora.we_work.dto.reservation.ReservationResponse;
import org.wora.we_work.entities.EspaceCoworking;
import org.wora.we_work.entities.Reservation;
import org.wora.we_work.entities.User;
import org.wora.we_work.exception.ResourceNotFoundException;
import org.wora.we_work.mapper.ReservationMapper;

import org.wora.we_work.repository.ReservationRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.wora.we_work.services.api.EquipementService;
import org.wora.we_work.services.api.EspaceCoworkingService;
import org.wora.we_work.services.api.UserService;
import org.wora.we_work.services.api.ValidationService;


import java.util.List;


import static org.mockito.ArgumentMatchers.anyLong;


@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private EspaceCoworkingService espaceCoworkingService;

    @Mock
    private EquipementService equipementService;

    @Mock
    private ValidationService validationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private User user;
    private EspaceCoworking espace;
    private EspaceCoworkingResponseDTO espaceDTO;
    private Reservation reservation;
    private ReservationRequest reservationRequest;
    private ReservationResponse reservationResponse;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        espace = new EspaceCoworking();
        espace.setId(1L);
        espace.setNom("Espace Test");

        LocalDateTime now = LocalDateTime.now();
        espaceDTO = new EspaceCoworkingResponseDTO(1L, 2L, List.of(1L, 2L), "Espace Test", "123 Rue Test", "Description de l'espace", 50.0, 10, List.of("image1.jpg"), true, now.minusDays(10), now);

        LocalDateTime dateDebut = LocalDateTime.now().plusDays(1);
        LocalDateTime dateFin = LocalDateTime.now().plusDays(2);

        reservationRequest = new ReservationRequest(1L, dateDebut, dateFin, List.of(1L, 2L), 5);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);
        reservation.setNombrePersonnes(5);
        reservation.setEspace(espace);
        reservation.setUser(user);
        reservation.setPrixTotal(new BigDecimal("200.00"));

        reservationResponse = new ReservationResponse(1L, "test@example.com", "Espace Test", 1L, dateDebut, dateFin, new BigDecimal("200.00"), 5);

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("Tests pour createReservation")
    class CreateReservationTests {

        @Test
        @DisplayName("Devrait créer une réservation avec succès")
        void shouldCreateReservationSuccessfully() {
            when(userService.getCurrentUser()).thenReturn(user);
            when(espaceCoworkingService.getById(anyLong())).thenReturn(espaceDTO);
            when(reservationMapper.espaceDtoToEntity(any(EspaceCoworkingResponseDTO.class))).thenReturn(espace);
            when(reservationMapper.toEntity(any(ReservationRequest.class))).thenReturn(reservation);
            when(equipementService.calculerPrixEquipements(anyList(), anyInt())).thenReturn(new BigDecimal("50.00"));
            when(espaceCoworkingService.calculerPrixBase(any(), anyInt(), any(), any())).thenReturn(new BigDecimal("150.00"));
            when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
            when(reservationMapper.toResponse(any(Reservation.class))).thenReturn(reservationResponse);

            ReservationResponse result = reservationService.createReservation(reservationRequest);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals(new BigDecimal("200.00"), result.prixTotal());

            verify(validationService).verifySpaceAvailability(espace, reservationRequest.dateDebut(), reservationRequest.dateFin());
            verify(equipementService).verifierDisponibiliteEquipements(reservationRequest.equipementIds());
            verify(reservationRepository).save(any(Reservation.class));
        }

        @Test
        @DisplayName("Devrait calculer correctement le prix total")
        void shouldCalculateTotalPriceCorrectly() {
            BigDecimal prixEquipements = new BigDecimal("75.50");
            BigDecimal prixBase = new BigDecimal("120.75");
            BigDecimal prixTotal = prixBase.add(prixEquipements);

            when(userService.getCurrentUser()).thenReturn(user);
            when(espaceCoworkingService.getById(anyLong())).thenReturn(espaceDTO);
            when(reservationMapper.espaceDtoToEntity(any())).thenReturn(espace);
            when(reservationMapper.toEntity(any())).thenReturn(reservation);
            when(equipementService.calculerPrixEquipements(anyList(), anyInt())).thenReturn(prixEquipements);
            when(espaceCoworkingService.calculerPrixBase(any(), anyInt(), any(), any())).thenReturn(prixBase);
            when(reservationRepository.save(any())).thenReturn(reservation);

            doAnswer(invocation -> {
                Reservation res = invocation.getArgument(0);
                assertEquals(prixTotal, res.getPrixTotal());
                return reservationResponse;
            }).when(reservationMapper).toResponse(any(Reservation.class));

            reservationService.createReservation(reservationRequest);

            verify(reservationRepository).save(argThat(res -> res.getPrixTotal().compareTo(prixTotal) == 0));
        }

        @Test
        @DisplayName("Devrait vérifier la disponibilité de l'espace")
        void shouldVerifySpaceAvailability() {
            when(userService.getCurrentUser()).thenReturn(user);
            when(espaceCoworkingService.getById(anyLong())).thenReturn(espaceDTO);
            when(reservationMapper.espaceDtoToEntity(any())).thenReturn(espace);
            when(reservationMapper.toEntity(any())).thenReturn(reservation);
            when(equipementService.calculerPrixEquipements(anyList(), anyInt())).thenReturn(new BigDecimal("50.00"));
            when(espaceCoworkingService.calculerPrixBase(any(), anyInt(), any(), any())).thenReturn(new BigDecimal("150.00"));
            when(reservationRepository.save(any())).thenReturn(reservation);
            when(reservationMapper.toResponse(any())).thenReturn(reservationResponse);

            reservationService.createReservation(reservationRequest);

            verify(validationService).verifySpaceAvailability(any(), any(), any());
        }

        @Test
        @DisplayName("Devrait vérifier la disponibilité des équipements")
        void shouldVerifyEquipmentAvailability() {

            when(userService.getCurrentUser()).thenReturn(user);
            when(espaceCoworkingService.getById(anyLong())).thenReturn(espaceDTO);
            when(reservationMapper.espaceDtoToEntity(any())).thenReturn(espace);
            when(reservationMapper.toEntity(any())).thenReturn(reservation);
                when(equipementService.calculerPrixEquipements(anyList(), anyInt())).thenReturn(new BigDecimal("50.00"));
            when(espaceCoworkingService.calculerPrixBase(any(), anyInt(), any(), any())).thenReturn(new BigDecimal("150.00"));
            when(reservationRepository.save(any())).thenReturn(reservation);
            when(reservationMapper.toResponse(any())).thenReturn(reservationResponse);

            reservationService.createReservation(reservationRequest);

            verify(equipementService).verifierDisponibiliteEquipements(reservationRequest.equipementIds());
        }
    }

    @Nested
    @DisplayName("Tests pour getReservationById")
    class GetReservationByIdTests {

        @Test
        @DisplayName("Devrait retourner une réservation existante")
        void shouldReturnExistingReservation() {


            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
            when(reservationMapper.toResponse(any(Reservation.class))).thenReturn(reservationResponse);

            ReservationResponse result = reservationService.getReservationById(1L);

            assertNotNull(result);
            assertEquals(1L, result.id());
            verify(reservationRepository).findById(1L);
        }

        @Test
        @DisplayName("Devrait lancer une exception pour une réservation non trouvée")
        void shouldThrowExceptionForNonExistingReservation() {

            when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> reservationService.getReservationById(99L));

            assertTrue(exception.getMessage().contains("Réservation non trouvée"));
            verify(reservationRepository).findById(99L);
        }
    }

    @Nested
    @DisplayName("Tests pour getAllReservations")
    class GetAllReservationsTests {

        @Test
        @DisplayName("Devrait retourner une page vide lorsqu'il n'y a pas de réservations")
        void shouldReturnEmptyPageWhenNoReservations() {

            when(reservationRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

            Page<ReservationResponse> result = reservationService.getAllReservations(pageable);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            assertEquals(0, result.getTotalElements());
            verify(reservationRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Devrait retourner une page de réservations")
        void shouldReturnPageOfReservations() {

            List<Reservation> reservations = List.of(reservation);
            Page<Reservation> reservationPage = new PageImpl<>(reservations, pageable, reservations.size());

            when(reservationRepository.findAll(any(Pageable.class))).thenReturn(reservationPage);
            when(reservationMapper.toResponse(any(Reservation.class))).thenReturn(reservationResponse);

            Page<ReservationResponse> result = reservationService.getAllReservations(pageable);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.getTotalElements());
            assertEquals(reservationResponse, result.getContent().getFirst());

            verify(reservationRepository).findAll(pageable);
            verify(reservationMapper).toResponse(reservation);
        }
    }

    @Nested
    @DisplayName("Tests pour getReservationsByUser")
    class GetReservationsByUserTests {

        @Test
        @DisplayName("Devrait retourner les réservations d'un utilisateur")
        void shouldReturnUserReservations() {
            List<Reservation> reservations = List.of(reservation);
            Page<Reservation> reservationPage = new PageImpl<>(reservations, pageable, reservations.size());

            when(reservationRepository.findByUserId(1L, pageable)).thenReturn(reservationPage);
            when(reservationMapper.toResponse(any(Reservation.class))).thenReturn(reservationResponse);


            Page<ReservationResponse> result = reservationService.getReservationsByUser(1L, pageable);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.getTotalElements());
            assertEquals(reservationResponse, result.getContent().getFirst());

            verify(reservationRepository).findByUserId(1L, pageable);
        }

        @Test
        @DisplayName("Devrait retourner une page vide si l'utilisateur n'a pas de réservations")
        void shouldReturnEmptyPageWhenUserHasNoReservations() {

            when(reservationRepository.findByUserId(1L, pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));


            Page<ReservationResponse> result = reservationService.getReservationsByUser(1L, pageable);


            assertNotNull(result);
            assertTrue(result.isEmpty());
            assertEquals(0, result.getTotalElements());

            verify(reservationRepository).findByUserId(1L, pageable);
        }
    }

    @Nested
    @DisplayName("Tests pour getReservationsByEspace")
    class GetReservationsByEspaceTests {

        @Test
        @DisplayName("Devrait retourner les réservations d'un espace")
        void shouldReturnEspaceReservations() {

            List<Reservation> reservations = List.of(reservation);
            Page<Reservation> reservationPage = new PageImpl<>(reservations, pageable, reservations.size());

            when(reservationRepository.findByEspaceId(1L, pageable)).thenReturn(reservationPage);
            when(reservationMapper.toResponse(any(Reservation.class))).thenReturn(reservationResponse);


            Page<ReservationResponse> result = reservationService.getReservationsByEspace(1L, pageable);


            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.getTotalElements());
            assertEquals(reservationResponse, result.getContent().getFirst());

            verify(reservationRepository).findByEspaceId(1L, pageable);
        }

        @Test
        @DisplayName("Devrait retourner une page vide si l'espace n'a pas de réservations")
        void shouldReturnEmptyPageWhenEspaceHasNoReservations() {


            when(reservationRepository.findByEspaceId(1L, pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));

            Page<ReservationResponse> result = reservationService.getReservationsByEspace(1L, pageable);


            assertNotNull(result);
            assertTrue(result.isEmpty());
            assertEquals(0, result.getTotalElements());

            verify(reservationRepository).findByEspaceId(1L, pageable);
        }
    }

    @Nested
    @DisplayName("Tests pour getReservationsForUser")
    class GetReservationsForUserTests {

        @Test
        @DisplayName("Devrait retourner les réservations pour un propriétaire d'espace")
        void shouldReturnReservationsForEspaceOwner() {


            List<Reservation> reservations = List.of(reservation);
            Page<Reservation> reservationPage = new PageImpl<>(reservations, pageable, reservations.size());

            when(reservationRepository.findByEspaceCoworkingUserId(1L, pageable)).thenReturn(reservationPage);
            when(reservationMapper.toResponse(any(Reservation.class))).thenReturn(reservationResponse);

            Page<ReservationResponse> result = reservationService.getReservationsForUser(1L, pageable);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.getTotalElements());
            assertEquals(reservationResponse, result.getContent().getFirst());

            verify(reservationRepository).findByEspaceCoworkingUserId(1L, pageable);
        }

        @Test
        @DisplayName("Devrait retourner une page vide si aucune réservation n'existe pour les espaces du propriétaire")
        void shouldReturnEmptyPageWhenNoReservationsExistForOwner() {

            when(reservationRepository.findByEspaceCoworkingUserId(1L, pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));

            Page<ReservationResponse> result = reservationService.getReservationsForUser(1L, pageable);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            assertEquals(0, result.getTotalElements());

            verify(reservationRepository).findByEspaceCoworkingUserId(1L, pageable);
        }
    }
}