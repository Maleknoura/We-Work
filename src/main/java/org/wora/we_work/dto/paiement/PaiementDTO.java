package org.wora.we_work.dto.paiement;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaiementDTO {
    private Long id;
    private Long reservationId;
    private BigDecimal montant;
    private String statut;
    private LocalDateTime datePaiement;
    private String methodePaiement;
}
