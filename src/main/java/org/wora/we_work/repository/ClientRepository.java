package org.wora.we_work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wora.we_work.entities.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
