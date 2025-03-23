package org.wora.we_work.dto.reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record ReservationResponse(Long id, String userName, String espaceNom, Long espaceId, LocalDateTime dateDebut,
                                  LocalDateTime dateFin, BigDecimal prixTotal,  Integer nombrePersonnes) {
}