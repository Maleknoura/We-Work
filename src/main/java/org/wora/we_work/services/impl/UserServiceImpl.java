package org.wora.we_work.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.wora.we_work.config.UserDetailsImpl;
import org.wora.we_work.entities.EspaceCoworking;
import org.wora.we_work.entities.User;
import org.wora.we_work.repository.EspaceCoworkingRepository;
import org.wora.we_work.repository.UserRepository;
import org.wora.we_work.services.api.EspaceCoworkingService;
import org.wora.we_work.services.api.UserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EspaceCoworkingRepository espaceCoworkingRepository;


    @Override
    public User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public boolean isUserBanned(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return !user.isEnabled();
    }

    public User getProprietaireByEspace(Long espaceId) {
        Optional<EspaceCoworking> espace = espaceCoworkingRepository.findById(espaceId);

        User user = espace.get().getUser();

        boolean isProprietaire = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_PROPRIETAIRE"));

        if (!isProprietaire) {
            throw new RuntimeException("L'utilisateur n'est pas un propriétaire valide");
        }

        return user;
    }

}
