package org.wora.we_work.services.api;

import com.stripe.exception.StripeException;
import org.wora.we_work.dto.paiement.PaiementDTO;
import org.wora.we_work.dto.paiement.PaiementIntentDTO;
import org.wora.we_work.dto.paiement.PaiementRequestDTO;

import java.util.List;

public interface PaiementService {
    PaiementIntentDTO creerIntentPaiement(PaiementRequestDTO requestDTO) throws StripeException;
    PaiementDTO confirmerPaiement(String paymentIntentId) throws StripeException;
    PaiementDTO getPaiement(Long id);
    List<PaiementDTO> getPaiementsByReservation(Long reservationId);
    PaiementDTO updateStatutPaiement(Long id, String statut);
}
