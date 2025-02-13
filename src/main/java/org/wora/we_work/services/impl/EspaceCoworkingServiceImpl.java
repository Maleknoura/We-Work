package org.wora.we_work.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingRequestDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.entities.EspaceCoworking;
import org.wora.we_work.entities.User;
import org.wora.we_work.exception.ResourceNotFoundException;
import org.wora.we_work.mapper.EspaceCoworkingMapper;
import org.wora.we_work.repository.EspaceCoworkingRepository;
import org.wora.we_work.services.api.EspaceCoworkingService;
import org.wora.we_work.services.api.UserService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EspaceCoworkingServiceImpl implements EspaceCoworkingService {
    private final EspaceCoworkingRepository espaceCoworkingRepository;
    private final EspaceCoworkingMapper espaceCoworkingMapper;
    private final UserService userService;

    private static final String ROLE_PROPRIETAIRE = "ROLE_PROPRIETAIRE";

    @Override
    public EspaceCoworkingResponseDTO create(EspaceCoworkingRequestDTO requestDTO) {
        User currentUser = userService.getCurrentUser();
        if (!hasProprietaireRole(currentUser)) {
            log.error("Tentative de création d'espace par un non-propriétaire: {}", currentUser.getEmail());
            throw new AccessDeniedException("Seuls les propriétaires peuvent créer des espaces");
        }

        EspaceCoworking espaceCoworking = espaceCoworkingMapper.toEntity(requestDTO);
        espaceCoworking.setUser(currentUser);
        espaceCoworking.setActive(true);

        validateEspaceCoworking(espaceCoworking);
        log.info("Création d'un nouvel espace par l'utilisateur: {}", currentUser.getEmail());

        return espaceCoworkingMapper.toResponseDTO(espaceCoworkingRepository.save(espaceCoworking));
    }

    @Override
    public EspaceCoworkingResponseDTO update(Long id, EspaceCoworkingRequestDTO requestDTO) {
        EspaceCoworking existingEspace = espaceCoworkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espace coworking non trouvé avec l'id: " + id));

        checkUserAccess(existingEspace);
        updateEspaceFields(existingEspace, requestDTO);
        validateEspaceCoworking(existingEspace);

        log.info("Mise à jour de l'espace id: {} par l'utilisateur: {}", id, userService.getCurrentUser().getEmail());
        return espaceCoworkingMapper.toResponseDTO(espaceCoworkingRepository.save(existingEspace));
    }

    @Override
    public void delete(Long id) {
        EspaceCoworking espace = espaceCoworkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espace coworking non trouvé avec l'id: " + id));

        checkUserAccess(espace);
        espace.setActive(false);
        espaceCoworkingRepository.save(espace);
        log.info("Désactivation de l'espace id: {} par l'utilisateur: {}", id, userService.getCurrentUser().getEmail());
    }

    @Override
    public EspaceCoworkingResponseDTO getById(Long id) {
        EspaceCoworking espace = espaceCoworkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espace coworking non trouvé avec l'id: " + id));
        return espaceCoworkingMapper.toResponseDTO(espace);
    }

    @Override
    public Page<EspaceCoworkingResponseDTO> getAll(Pageable pageable) {
        return espaceCoworkingRepository.findAll(pageable).map(espaceCoworkingMapper::toResponseDTO);
    }

    @Override
    public Page<EspaceCoworkingResponseDTO> getAllByProprietaire(Long proprietaireId, Pageable pageable) {
        return espaceCoworkingRepository.findByUserId(proprietaireId, pageable)
                .map(espaceCoworkingMapper::toResponseDTO);
    }

    private void updateEspaceFields(EspaceCoworking espace, EspaceCoworkingRequestDTO requestDTO) {
        espace.setNom(requestDTO.getNom());
        espace.setAdresse(requestDTO.getAdresse());
        espace.setDescription(requestDTO.getDescription());
        espace.setPrixParJour(requestDTO.getPrixParJour());
        espace.setCapacite(requestDTO.getCapacite());
    }

    private void checkUserAccess(EspaceCoworking espace) {
        User currentUser = userService.getCurrentUser();
        boolean isAdmin = hasAdminRole(currentUser);
        boolean isOwner = espace.getUser().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            log.warn("Tentative d'accès non autorisé à l'espace id: {} par l'utilisateur: {}",
                    espace.getId(), currentUser.getEmail());
            throw new AccessDeniedException("Vous n'avez pas les droits pour modifier cet espace");
        }
    }

    private boolean hasProprietaireRole(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> ROLE_PROPRIETAIRE.equals(role.getName()));
    }

    private boolean hasAdminRole(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
    }

    private void validateEspaceCoworking(EspaceCoworking espace) {
        if (espace.getPrixParJour() <= 0) {
            throw new IllegalArgumentException("Le prix par jour doit être supérieur à 0");
        }
        if (espace.getCapacite() <= 0) {
            throw new IllegalArgumentException("La capacité doit être supérieure à 0");
        }
    }
}