package org.wora.we_work.services.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wora.we_work.dto.paiement.PaiementDTO;
import org.wora.we_work.dto.paiement.PaiementIntentDTO;
import org.wora.we_work.dto.paiement.PaiementRequestDTO;
import org.wora.we_work.entities.Paiement;
import org.wora.we_work.entities.Reservation;
import org.wora.we_work.entities.User;
import org.wora.we_work.mapper.PaiementMapper;
import org.wora.we_work.repository.PaiementRepository;
import org.wora.we_work.repository.ReservationRepository;
import org.wora.we_work.services.api.PaiementService;
import org.wora.we_work.services.api.UserService;

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

    private static final String REQUIRES_CAPTURE = "requires_capture";
    private static final String REQUIRES_PAYMENT_METHOD = "requires_payment_method";
    private static final String STATUS_SUCCEEDED = "succeeded";
    private static final String STATUS_PROCESSING = "processing";

    private static final String CURRENCY = "eur";
    private static final Map<String, Object> AUTOMATIC_PAYMENT_METHODS = Map.of(
            "enabled", true,
            "allow_redirects", "never"
    );
    private static final String DESCRIPTION_PREFIX = "Paiement pour réservation #";

    private static final String STATUT_COMPLETE = "COMPLETE";
    private static final String METHODE_PAIEMENT_CARTE = "CARTE";

    private final PaiementRepository paiementRepository;
    private final ReservationRepository reservationRepository;
    private final PaiementMapper paiementMapper;

    @Override
    @Transactional
    public PaiementIntentDTO creerIntentPaiement(PaiementRequestDTO requestDTO) throws StripeException {
        Reservation reservation = getReservationById(requestDTO.getReservationId());
        Long montantEnCentimes = convertirMontantEnCentimes(requestDTO.getMontant());

        Map<String, Object> params = creerParametresPaiement(montantEnCentimes, reservation);
        PaymentIntent paymentIntent = PaymentIntent.create(params);
        log.info("PaymentIntent créé : {}", paymentIntent);

        return buildPaiementIntentDTO(requestDTO, paymentIntent, reservation);
    }

    @Override
    @Transactional
    public PaiementDTO confirmerPaiement(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        log.info("Statut du PaymentIntent : {}", paymentIntent.getStatus());

        if (isStatutValide(paymentIntent.getStatus())) {
            if (REQUIRES_CAPTURE.equals(paymentIntent.getStatus())) {
                paymentIntent = paymentIntent.capture();
            }

            Paiement paiement = creerPaiement(paymentIntent);
            Paiement savedPaiement = paiementRepository.save(paiement);
            log.info("Paiement enregistré avec succès : {}", savedPaiement.getId());

            return paiementMapper.toDTO(savedPaiement);
        } else if (REQUIRES_PAYMENT_METHOD.equals(paymentIntent.getStatus())) {
            paymentIntent = confirmerAvecNouvelleMethode(paymentIntent);
            return confirmerPaiement(paymentIntentId);
        } else {
            log.warn("Paiement non confirmé. Statut : {}", paymentIntent.getStatus());
            throw new RuntimeException("Le paiement n'a pas été confirmé. Statut : " + paymentIntent.getStatus());
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


    private Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation non trouvée"));
    }

    private Long convertirMontantEnCentimes(BigDecimal montant) {
        return montant.multiply(new BigDecimal("100")).longValue();
    }

    private Map<String, Object> creerParametresPaiement(Long montantEnCentimes, Reservation reservation) {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", montantEnCentimes);
        params.put("currency", CURRENCY);
        params.put("description", DESCRIPTION_PREFIX + reservation.getId());
        params.put("automatic_payment_methods", AUTOMATIC_PAYMENT_METHODS);
        return params;
    }

    private PaiementIntentDTO buildPaiementIntentDTO(PaiementRequestDTO requestDTO, PaymentIntent paymentIntent, Reservation reservation) {
        return PaiementIntentDTO.builder()
                .clientSecret(paymentIntent.getClientSecret())
                .paymentIntentId(paymentIntent.getId())
                .montant(requestDTO.getMontant())
                .devise("EUR")
                .reservationId(reservation.getId())
                .build();
    }

    private boolean isStatutValide(String statut) {
        return List.of(STATUS_SUCCEEDED, REQUIRES_CAPTURE, STATUS_PROCESSING).contains(statut);
    }

    private Paiement creerPaiement(PaymentIntent paymentIntent) {
        Paiement paiement = new Paiement();
        paiement.setMontant(new BigDecimal(paymentIntent.getAmount()).divide(new BigDecimal("100")));
        paiement.setStatut(STATUT_COMPLETE);
        paiement.setMethodePaiement(METHODE_PAIEMENT_CARTE);
        paiement.setDatePaiement(LocalDateTime.now());

        Long reservationId = extraireReservationId(paymentIntent.getDescription());
        Reservation reservation = getReservationById(reservationId);
        paiement.setReservation(reservation);

        return paiement;
    }

    private Long extraireReservationId(String description) {
        return Long.parseLong(description.split("#")[1]);
    }

    private PaymentIntent confirmerAvecNouvelleMethode(PaymentIntent paymentIntent) throws StripeException {
        Map<String, Object> confirmParams = new HashMap<>();
        confirmParams.put("payment_method", "pm_card_visa");
        confirmParams.put("return_url", "http://localhost:8081/payment/return");
        return paymentIntent.confirm(confirmParams);
    }
}