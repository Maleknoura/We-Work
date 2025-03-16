package org.wora.we_work.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wora.we_work.dto.AbonnementDTO;
import org.wora.we_work.dto.UserDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingDTO;
import org.wora.we_work.dto.paiement.PaiementDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record ReservationResponse(
        Long id ,
        String userName,
        String espaceNom,
        Long espaceId,
        LocalDateTime dateDebut,
        LocalDateTime dateFin,
        BigDecimal prixTotal,
        String statut,
        Integer nombrePersonnes
) {}