package org.wora.we_work.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wora.we_work.dto.AbonnementDTO;
import org.wora.we_work.dto.PaiementDTO;
import org.wora.we_work.dto.UserDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private UserDTO user;
    private EspaceCoworkingDTO espaceCoworking;
    private AbonnementDTO abonnement;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String statut;
    private BigDecimal prixTotal;
    private Integer nombrePersonnes;
    private PaiementDTO paiement;
}