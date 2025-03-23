package org.wora.we_work.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wora.we_work.entities.Reservation;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findByUserId(Long userId, Pageable pageable);

    Page<Reservation> findByEspaceId(Long espaceId, Pageable pageable);


    @Query("SELECT r FROM Reservation r WHERE r.espace.id = :espaceId " + "AND ((r.dateDebut BETWEEN :dateDebut AND :dateFin) " + "OR (r.dateFin BETWEEN :dateDebut AND :dateFin) " + "OR (:dateDebut BETWEEN r.dateDebut AND r.dateFin))")
    List<Reservation> findOverlappingReservations(@Param("espaceId") Long espaceId, @Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.espace.user.id = :userId")
    Long countByUserId(Long userId);

    @Query("SELECT r FROM Reservation r JOIN r.espace e WHERE e.user.id = :userId")
    Page<Reservation> findByEspaceCoworkingUserId(@Param("userId") Long userId, Pageable pageable);


    @Query("SELECT COUNT(r) FROM Reservation r JOIN r.espace e WHERE e.user.id = :ownerId AND YEAR(r.dateDebut) = :year AND MONTH(r.dateDebut) = :month")
    int countByOwnerIdAndYearAndMonth(@Param("ownerId") Long ownerId, @Param("year") int year, @Param("month") int month);
}