package org.wora.we_work.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaiementDTO {
    private Long id;
    private String statut;
    private BigDecimal montant;
    private LocalDateTime datePaiement;
}
