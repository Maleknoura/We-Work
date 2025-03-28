package org.wora.we_work.services.api;

import org.wora.we_work.entities.User;

import java.util.Optional;

public interface UserService {
    User getCurrentUser();

    Long getUserIdByEmail(String email);

    Long getUserIdByUsername(String username);

    void banUser(Long userId);

    void unbanUser(Long userId);

    boolean isUserBanned(String username);

    User getProprietaireByEspace(Long espaceId);
}
