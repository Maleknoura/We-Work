package org.wora.we_work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wora.we_work.entities.Avis;

import java.util.List;

public interface AvisRepository extends JpaRepository<Avis,Long> {
    List<Avis> findByEspaceCoworkingId(Long espaceCoworkingId);

    @Query("SELECT AVG(a.stars) FROM Avis a WHERE a.espaceCoworking.id = :coworkingSpaceId")
    Double getAverageRatingByCoworkingSpaceId(Long coworkingSpaceId);
}
