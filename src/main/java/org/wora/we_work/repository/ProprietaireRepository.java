package org.wora.we_work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wora.we_work.entities.Proprietaire;
import org.wora.we_work.entities.User;

import java.util.Optional;


@Repository
public interface ProprietaireRepository extends JpaRepository<Proprietaire, Long> {
    Optional<Proprietaire> findById(Long id);

    Optional<Proprietaire> findByUsername(String username);

    Optional<Proprietaire> findByEmail(String email);

}

