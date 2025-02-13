package org.wora.we_work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wora.we_work.entities.Reservation;
import org.wora.we_work.entities.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByDateDebutBetween(LocalDateTime start, LocalDateTime end);

    List<Reservation> findByUserId(Long userId);

    @Query("SELECT r FROM Reservation r WHERE r.espace.id = :espaceId " +
            "AND ((r.dateDebut BETWEEN :dateDebut AND :dateFin) " +
            "OR (r.dateFin BETWEEN :dateDebut AND :dateFin) " +
            "OR (:dateDebut BETWEEN r.dateDebut AND r.dateFin))")
    List<Reservation> findOverlappingReservations(
            @Param("espaceId") Long espaceId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );
}
