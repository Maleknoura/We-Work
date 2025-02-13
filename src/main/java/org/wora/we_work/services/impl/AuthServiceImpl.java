package org.wora.we_work.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wora.we_work.dto.*;
import org.wora.we_work.entities.Role;
import org.wora.we_work.entities.User;
import org.wora.we_work.exception.EmailAlreadyExistsException;
import org.wora.we_work.exception.UsernameAlreadyExistsException;
import org.wora.we_work.repository.RoleRepository;
import org.wora.we_work.repository.UserRepository;
import org.wora.we_work.security.JwtTokenProvider;
import org.wora.we_work.services.api.AuthService;
import org.wora.we_work.services.api.UserService;

import javax.management.relation.RoleNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    private static final String ROLE_CLIENT = "ROLE_CLIENT";
    private static final String ROLE_PROPRIETAIRE = "ROLE_PROPRIETAIRE";

    @Override
    public AuthResponseDTO register(RegisterRequestDTO request) throws RoleNotFoundException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Cet email est déjà utilisé");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Ce nom d'utilisateur est déjà utilisé");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setRoles(new HashSet<>());

        String roleType = "ROLE_" + request.getUserType();
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RoleNotFoundException("Rôle non trouvé: " + roleType));
        user.getRoles().add(role);

        if (ROLE_PROPRIETAIRE.equals(roleType)) {
            user.setTotalAmount(0.0);
        }

        user = userRepository.save(user);

        String token = tokenProvider.createToken(user.getEmail());
        LocalDateTime expirationDateTime = getTokenExpirationDateTime(token);

        return createAuthResponse(token, user, expirationDateTime);
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

            String token = tokenProvider.createToken(user.getEmail());
            LocalDateTime expirationDateTime = getTokenExpirationDateTime(token);

            return createAuthResponse(token, user, expirationDateTime);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }
    }

    @Override
    public void completeClientProfile(ClientProfileDTO profileDTO) {
        User currentUser = userService.getCurrentUser();
        if (!hasRole(currentUser, ROLE_CLIENT)) {
            throw new AccessDeniedException("L'utilisateur n'est pas un client");
        }

        currentUser.setPhoneNumber(profileDTO.getPhoneNumber());
        userRepository.save(currentUser);
    }

    @Override
    public void completeProprietaireProfile(ProprietaireProfileDTO profileDTO) {
        User currentUser = userService.getCurrentUser();
        if (!hasRole(currentUser, ROLE_PROPRIETAIRE)) {
            throw new AccessDeniedException("L'utilisateur n'est pas un propriétaire");
        }

        currentUser.setCompanyName(profileDTO.getCompanyName());
        currentUser.setPhoneNumber(profileDTO.getPhoneNumber());
        currentUser.setSiretNumber(profileDTO.getSiretNumber());

        if (currentUser.getTotalAmount() == null) {
            currentUser.setTotalAmount(0.0);
        }

        userRepository.save(currentUser);
    }

    private LocalDateTime getTokenExpirationDateTime(String token) {
        return tokenProvider.getExpirationDate(token).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public boolean isProfileComplete(User user) {
        if (hasRole(user, ROLE_CLIENT)) {
            return user.getPhoneNumber() != null;
        } else if (hasRole(user, ROLE_PROPRIETAIRE)) {
            return user.getCompanyName() != null &&
                    user.getPhoneNumber() != null &&
                    user.getSiretNumber() != null;
        }
        return false;
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> roleName.equals(role.getName()));
    }

    private AuthResponseDTO createAuthResponse(String token, User user, LocalDateTime expirationDateTime) {
        String userType = determineUserType(user);
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new AuthResponseDTO(
                token,
                userType,
                user.getEmail(),
                user.getId(),
                roles,
                expirationDateTime
        );
    }

    private String determineUserType(User user) {
        if (hasRole(user, ROLE_PROPRIETAIRE)) {
            return "PROPRIETAIRE";
        } else if (hasRole(user, ROLE_CLIENT)) {
            return "CLIENT";
        }
        return "USER";
    }
}