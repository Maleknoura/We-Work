package org.wora.we_work.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wora.we_work.entities.EspaceCoworking;

import java.util.List;
import java.util.Optional;

public interface EspaceCoworkingRepository extends JpaRepository<EspaceCoworking,Long> {

    Optional<EspaceCoworking> findByIdAndActiveTrue(Long id);

    Page<EspaceCoworking> findAllByActiveTrue(Pageable pageable);

    Page<EspaceCoworking> findByUserIdAndActiveTrue(Long proprietaireId, Pageable pageable);

    Page<EspaceCoworking> findByUserId(Long userId, Pageable pageable);

}
