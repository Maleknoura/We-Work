package org.wora.we_work.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.wora.we_work.entities.User;
import org.wora.we_work.repository.UserRepository;
import org.wora.we_work.services.api.UserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Utilisateur non authentifié");
        }

        return (User) authentication.getPrincipal();
    }
    @Override
    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username))
                .getId();
    }
    @Override
    public Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour l'email: " + email));
        return user.getId();
    }
    @Override
    public void updateVerificationStatus(String username, String status) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        userOptional.ifPresent(user -> {
            user.setVerificationStatus(status);
            userRepository.save(user);
        });
    }
}
