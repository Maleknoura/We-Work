package org.wora.we_work.services.api;


import org.wora.we_work.dto.AuthRequestDTO;
import org.wora.we_work.dto.AuthResponseDTO;
import org.wora.we_work.entities.User;

public interface UserService {
    AuthResponseDTO registerNewUser(AuthRequestDTO request);
}

