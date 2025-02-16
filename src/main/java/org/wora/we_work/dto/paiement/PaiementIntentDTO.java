package org.wora.we_work.dto.paiement;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaiementIntentDTO {
    private String clientSecret;
    private String paymentIntentId;
    private BigDecimal montant;
    private String devise;
    private Long reservationId;
}
