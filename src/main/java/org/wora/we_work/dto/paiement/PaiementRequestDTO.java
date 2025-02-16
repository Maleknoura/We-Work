package org.wora.we_work.dto.paiement;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaiementRequestDTO {
    private Long reservationId;
    private BigDecimal montant;
    private String methodePaiement;
}
