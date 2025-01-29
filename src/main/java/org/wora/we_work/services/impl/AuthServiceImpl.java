package org.wora.we_work.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.wora.we_work.dto.*;
import org.wora.we_work.entities.Client;
import org.wora.we_work.entities.Proprietaire;
import org.wora.we_work.entities.Role;
import org.wora.we_work.entities.User;
import org.wora.we_work.exception.EmailAlreadyExistsException;
import org.wora.we_work.repository.ClientRepository;
import org.wora.we_work.repository.ProprietaireRepository;
import org.wora.we_work.repository.RoleRepository;
import org.wora.we_work.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wora.we_work.security.JwtTokenProvider;
import org.wora.we_work.services.api.AuthService;
import org.wora.we_work.services.api.UserService;

import javax.management.relation.RoleNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final ProprietaireRepository proprietaireRepository;
    private final ClientRepository clientRepository;
    private final UserService userService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            RoleRepository roleRepository,
            AuthenticationManager authenticationManager,
            ProprietaireRepository proprietaireRepository,
            ClientRepository clientRepository,
            UserService userService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.proprietaireRepository = proprietaireRepository;
        this.clientRepository = clientRepository;
        this.userService = userService;
    }

    @Override
    public AuthResponseDTO register(RegisterRequestDTO request) throws RoleNotFoundException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Cet email est déjà utilisé");
        }

        User user;
        String roleType = "ROLE_" + request.getUserType();

        if ("ROLE_CLIENT".equals(roleType)) {
            user = new Client();
        } else if ("ROLE_PROPRIETAIRE".equals(roleType)) {
            user = new Proprietaire();
        } else {
            user = new User();
        }

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);


        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RoleNotFoundException("Rôle non trouvé: " + roleType));
        user.getRoles().add(role);

        user = userRepository.save(user);

        String token = tokenProvider.createToken(user.getUsername());
        LocalDateTime expirationDateTime = getTokenExpirationDateTime(token);

        return createAuthResponse(token, user, expirationDateTime);
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticateUser(request);
        User user = (User) authentication.getPrincipal();
        String token = tokenProvider.createToken(user.getUsername());
        LocalDateTime expirationDateTime = getTokenExpirationDateTime(token);

        return createAuthResponse(token, user, expirationDateTime);
    }

    @Override
    public void completeClientProfile(ClientProfileDTO profileDTO) {
        User currentUser = userService.getCurrentUser();
        validateUserType(currentUser, Client.class, "L'utilisateur n'est pas un client");

        Client client = (Client) currentUser;
        client.setPhoneNumber(profileDTO.getPhoneNumber());
        clientRepository.save(client);
    }

    @Override
    public void completeProprietaireProfile(ProprietaireProfileDTO profileDTO) {
        User currentUser = userService.getCurrentUser();
        validateUserType(currentUser, Proprietaire.class, "L'utilisateur n'est pas un propriétaire");

        Proprietaire proprietaire = (Proprietaire) currentUser;
        updateProprietaireProfile(proprietaire, profileDTO);
        proprietaireRepository.save(proprietaire);
    }

    public boolean isProfileComplete(User user) {
        if (user instanceof Client client) {
            return isClientProfileComplete(client);
        } else if (user instanceof Proprietaire proprietaire) {
            return isProprietaireProfileComplete(proprietaire);
        }
        return false;
    }

    private User createUserByType(String userType) {
        return switch ("ROLE_" + userType) {
            case "ROLE_CLIENT" -> new Client();
            case "ROLE_PROPRIETAIRE" -> new Proprietaire();
            default -> new User();
        };
    }

    private void setupUserBasicInfo(User user, RegisterRequestDTO request) {
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
    }

    private void assignUserRole(User user, String userType) throws RoleNotFoundException {
        String roleType = "ROLE_" + userType;
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RoleNotFoundException("Rôle non trouvé: " + roleType));
        user.getRoles().add(role);
    }

    private Authentication authenticateUser(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    private LocalDateTime getTokenExpirationDateTime(String token) {
        return tokenProvider.getExpirationDate(token).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private void updateProprietaireProfile(Proprietaire proprietaire, ProprietaireProfileDTO profileDTO) {
        proprietaire.setCompanyName(profileDTO.getCompanyName());
        proprietaire.setPhoneNumber(profileDTO.getPhoneNumber());
        proprietaire.setSiretNumber(profileDTO.getSiretNumber());

        if (proprietaire.getTotalAmount() == null) {
            proprietaire.setTotalAmount(0.0);
        }
    }

    private <T> void validateUserType(User user, Class<T> expectedType, String errorMessage) {
        if (!expectedType.isInstance(user)) {
            throw new AccessDeniedException(errorMessage);
        }
    }

    private boolean isClientProfileComplete(Client client) {
        return client.getPhoneNumber() != null;
    }

    private boolean isProprietaireProfileComplete(Proprietaire proprietaire) {
        return proprietaire.getCompanyName() != null &&
                proprietaire.getPhoneNumber() != null &&
                proprietaire.getSiretNumber() != null;
    }

    private AuthResponseDTO createAuthResponse(String token, User user, LocalDateTime expirationDateTime) {
        String userType = user instanceof Client ? "CLIENT" : "PROPRIETAIRE";
        return new AuthResponseDTO(
                token,
                userType,
                user.getUsername(),
                user.getId(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
                expirationDateTime
        );
    }
}

