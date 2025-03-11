package org.wora.we_work.services.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.identity.VerificationSession;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@Getter
public class StripeIdentityService {
    @Value("${stripe.identity.secret.key}")
    private String stripeApiKey;

    public Map<String, String> createVerificationSession() throws StripeException {
        Stripe.apiKey = stripeApiKey;

        Map<String, Object> verificationSessionParams = new HashMap<>();
        verificationSessionParams.put("type", "document");
        verificationSessionParams.put("return_url", "http://localhost:8081/api/identity/verification-callback?success=true");

        Map<String, Object> options = new HashMap<>();
        Map<String, Object> documentOptions = new HashMap<>();
        documentOptions.put("require_id_number", false);
        documentOptions.put("require_matching_selfie", false);
        options.put("document", documentOptions);

        verificationSessionParams.put("options", options);

        VerificationSession session = VerificationSession.create(verificationSessionParams);

        Map<String, String> response = new HashMap<>();
        response.put("url", session.getUrl());
        response.put("id", session.getId());
        return response;
    }
    public VerificationSession retrieveVerificationSession(String sessionId) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        return VerificationSession.retrieve(sessionId);
    }
}



