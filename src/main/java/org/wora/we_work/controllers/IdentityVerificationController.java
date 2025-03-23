package org.wora.we_work.controllers;

import com.stripe.exception.StripeException;
import com.stripe.model.identity.VerificationSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wora.we_work.services.api.UserService;
import org.wora.we_work.services.impl.StripeIdentityService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/identity")
@AllArgsConstructor
public class IdentityVerificationController {
    private StripeIdentityService stripeIdentityService;
    private static final String STATUS_VERIFIED = "verified";
    private static final String STATUS_REQUIRES_INPUT = "requires_input";

    @PostMapping("/start-verification")
    public ResponseEntity<Map<String, Object>> startVerification() {
        try {
            Map<String, String> sessionData = stripeIdentityService.createVerificationSession();
            Map<String, Object> response = new HashMap<>();
            response.put("verification_url", sessionData.get("url"));
            response.put("session_id", sessionData.get("id"));
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/verification-callback")
    public ResponseEntity<String> handleCallback(@RequestParam(required = false) Boolean success, @RequestParam(required = false) String session_id) {

        if (success != null && success) {
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "http://localhost:8081/verification-success.html").build();
        } else {
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "http://localhost:8081/verification-failed.html").build();
        }
    }

    @GetMapping("/verification-status/{sessionId}")
    public ResponseEntity<Map<String, String>> checkVerificationStatus(@PathVariable String sessionId) throws StripeException {
        VerificationSession session = stripeIdentityService.retrieveVerificationSession(sessionId);
        Map<String, String> response = creerReponseVerification(sessionId, session.getStatus());
        return ResponseEntity.ok(response);
    }

    private Map<String, String> creerReponseVerification(String sessionId, String status) {
        Map<String, String> response = new HashMap<>();
        response.put("status", status);
        response.put("session_id", sessionId);

        if (STATUS_VERIFIED.equals(status)) {
            response.put("message", "Verification completed successfully");
        } else if (STATUS_REQUIRES_INPUT.equals(status)) {
            response.put("message", "Waiting for user to complete verification");
        } else {
            response.put("message", "Verification status: " + status);
        }

        return response;
    }
}


