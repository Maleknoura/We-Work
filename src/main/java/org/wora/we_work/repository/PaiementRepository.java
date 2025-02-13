package org.wora.we_work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wora.we_work.entities.Paiement;

public interface PaiementRepository extends JpaRepository<Paiement,Long> {
}
