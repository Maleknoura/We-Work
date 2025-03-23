package org.wora.we_work.services.api;


import org.wora.we_work.dto.user.AuthResponseDTO;
import org.wora.we_work.dto.user.LoginRequestDTO;
import org.wora.we_work.dto.user.RegisterRequestDTO;

import javax.management.relation.RoleNotFoundException;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO request) throws RoleNotFoundException;

    AuthResponseDTO login(LoginRequestDTO request);

}

