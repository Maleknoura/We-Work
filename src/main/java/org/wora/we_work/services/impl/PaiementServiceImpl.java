package org.wora.we_work.services.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wora.we_work.dto.paiement.PaiementDTO;
import org.wora.we_work.dto.paiement.PaiementIntentDTO;
import org.wora.we_work.dto.paiement.PaiementRequestDTO;
import org.wora.we_work.entities.Paiement;
import org.wora.we_work.entities.Reservation;
import org.wora.we_work.mapper.PaiementMapper;
import org.wora.we_work.repository.PaiementRepository;
import org.wora.we_work.repository.ReservationRepository;
import org.wora.we_work.services.api.PaiementService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;
    private final ReservationRepository reservationRepository;
    private final PaiementMapper paiementMapper;

    @Override
    @Transactional
    public PaiementIntentDTO creerIntentPaiement(PaiementRequestDTO requestDTO) throws StripeException {
        Reservation reservation = reservationRepository.findById(requestDTO.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation non trouvée"));

        Long montantEnCentimes = requestDTO.getMontant().multiply(new BigDecimal("100")).longValue();

        Map<String, Object> params = new HashMap<>();
        params.put("amount", montantEnCentimes);
        params.put("currency", "eur");
        params.put("description", "Paiement pour réservation #" + reservation.getId());

        Map<String, Object> automaticPaymentMethods = new HashMap<>();
        automaticPaymentMethods.put("enabled", true);
        automaticPaymentMethods.put("allow_redirects", "never");
        params.put("automatic_payment_methods", automaticPaymentMethods);

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        log.info("Created PaymentIntent with ID: {} and status: {}", paymentIntent.getId(), paymentIntent.getStatus());

        PaiementIntentDTO intentDTO = new PaiementIntentDTO();
        intentDTO.setClientSecret(paymentIntent.getClientSecret());
        intentDTO.setPaymentIntentId(paymentIntent.getId());
        intentDTO.setMontant(requestDTO.getMontant());
        intentDTO.setDevise("EUR");
        intentDTO.setReservationId(requestDTO.getReservationId());

        return intentDTO;
    }

    @Override
    @Transactional
    public PaiementDTO confirmerPaiement(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        log.info("Payment Intent status: {}", paymentIntent.getStatus());
        log.info("Payment Intent ID: {}", paymentIntent.getId());

        List<String> validStatuses = List.of("succeeded", "requires_capture", "processing");

        if (validStatuses.contains(paymentIntent.getStatus())) {
            if ("requires_capture".equals(paymentIntent.getStatus())) {
                paymentIntent = paymentIntent.capture();
            }

            try {
                Paiement paiement = new Paiement();
                paiement.setMontant(new BigDecimal(paymentIntent.getAmount()).divide(new BigDecimal("100")));
                paiement.setStatut("COMPLETE");
                paiement.setMethodePaiement("CARTE");
                paiement.setDatePaiement(LocalDateTime.now());

                String description = paymentIntent.getDescription();
                Long reservationId = Long.parseLong(description.split("#")[1]);
                Reservation reservation = reservationRepository.findById(reservationId)
                        .orElseThrow(() -> new RuntimeException("Reservation non trouvée"));
                paiement.setReservation(reservation);

                Paiement savedPaiement = paiementRepository.save(paiement);
                log.info("Payment saved successfully with ID: {}", savedPaiement.getId());
                return paiementMapper.toDTO(savedPaiement);
            } catch (Exception e) {
                log.error("Error while saving payment: {}", e.getMessage());
                throw new RuntimeException("Erreur lors de l'enregistrement du paiement: " + e.getMessage());
            }
        } else if ("requires_payment_method".equals(paymentIntent.getStatus())) {
            Map<String, Object> confirmParams = new HashMap<>();
            confirmParams.put("payment_method", "pm_card_visa");
            confirmParams.put("return_url", "http://localhost:8081/payment/return");

            paymentIntent = paymentIntent.confirm(confirmParams);
            return confirmerPaiement(paymentIntentId);
        } else {
            log.warn("Payment not confirmed. Status: {}", paymentIntent.getStatus());
            throw new RuntimeException("Le paiement n'a pas été confirmé. Statut: " + paymentIntent.getStatus());
        }
    }

    @Override
    public PaiementDTO getPaiement(Long id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));
        return paiementMapper.toDTO(paiement);
    }

    @Override
    public List<PaiementDTO> getPaiementsByReservation(Long reservationId) {
        List<Paiement> paiements = paiementRepository.findByReservationId(reservationId);
        return paiements.stream()
                .map(paiementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaiementDTO updateStatutPaiement(Long id, String statut) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));
        paiement.setStatut(statut);
        return paiementMapper.toDTO(paiementRepository.save(paiement));
    }
}