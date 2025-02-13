package org.wora.we_work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wora.we_work.entities.Abonnement;
import org.wora.we_work.entities.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
    List<Abonnement> findByUser(User user);
    List<Abonnement> findByDateFinAfter(LocalDate date);
}
