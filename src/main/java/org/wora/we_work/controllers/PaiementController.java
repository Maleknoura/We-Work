package org.wora.we_work.controllers;

import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wora.we_work.dto.paiement.PaiementDTO;
import org.wora.we_work.dto.paiement.PaiementIntentDTO;
import org.wora.we_work.dto.paiement.PaiementRequestDTO;
import org.wora.we_work.services.api.PaiementService;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;
    @Value("${stripe.public.key}")
    private String stripePublicKey;

    @GetMapping("/key/stripe-public")
    public ResponseEntity<String> getStripePublicKey() {
        return ResponseEntity.ok(stripePublicKey);
    }
    @PostMapping("/intent")
    public ResponseEntity<PaiementIntentDTO> creerIntentPaiement(@RequestBody PaiementRequestDTO requestDTO) throws StripeException {
        return ResponseEntity.ok(paiementService.creerIntentPaiement(requestDTO));
    }

    @PostMapping("/confirm/{paymentIntentId}")
    public ResponseEntity<PaiementDTO> confirmerPaiement(@PathVariable String paymentIntentId) throws StripeException {
        return ResponseEntity.ok(paiementService.confirmerPaiement(paymentIntentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaiementDTO> getPaiement(@PathVariable Long id) {
        return ResponseEntity.ok(paiementService.getPaiement(id));
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<PaiementDTO>> getPaiementsByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(paiementService.getPaiementsByReservation(reservationId));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<PaiementDTO> updateStatutPaiement(
            @PathVariable Long id,
            @RequestParam String statut) {
        return ResponseEntity.ok(paiementService.updateStatutPaiement(id, statut));
    }
}
