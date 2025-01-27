package org.wora.we_work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wora.we_work.entities.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
}
