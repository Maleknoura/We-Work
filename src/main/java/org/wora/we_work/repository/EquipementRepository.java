package org.wora.we_work.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.wora.we_work.entities.Equipement;
import org.wora.we_work.entities.EspaceCoworking;

import java.util.List;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {
    Page<Equipement> findByEspaceId(Long espaceId, Pageable pageable);
    boolean existsByIdAndEspaceId(Long id, Long espaceCoworkingId);

    List<Equipement> findByEspace(EspaceCoworking espace);
    @Query("SELECT e FROM Equipement e JOIN e.espace esp WHERE esp.user.id = :userId")
    List<Equipement> findAllEquipementsByUserId(@Param("userId") Long userId);
}

