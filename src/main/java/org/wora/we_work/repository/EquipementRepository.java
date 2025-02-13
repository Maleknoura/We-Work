package org.wora.we_work.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wora.we_work.entities.Equipement;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {
    Page<Equipement> findByEspaceCoworkingId(Long espaceCoworkingId, Pageable pageable);
    boolean existsByIdAndEspaceCoworkingId(Long id, Long espaceCoworkingId);
}

