package org.wora.we_work.services.api;


import org.wora.we_work.dto.*;
import org.wora.we_work.entities.User;

import javax.management.relation.RoleNotFoundException;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO request) throws RoleNotFoundException;
    AuthResponseDTO login(LoginRequestDTO request);

    void completeClientProfile(ClientProfileDTO profileDTO);

    void completeProprietaireProfile(ProprietaireProfileDTO profileDTO);
}

