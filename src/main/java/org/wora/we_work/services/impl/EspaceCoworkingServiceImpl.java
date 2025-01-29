package org.wora.we_work.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingRequestDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.entities.EspaceCoworking;
import org.wora.we_work.entities.Proprietaire;
import org.wora.we_work.entities.User;
import org.wora.we_work.exception.ResourceNotFoundException;
import org.wora.we_work.mapper.EspaceCoworkingMapper;
import org.wora.we_work.repository.EspaceCoworkingRepository;
import org.wora.we_work.repository.ProprietaireRepository;
import org.wora.we_work.services.api.EspaceCoworkingService;
import org.wora.we_work.services.api.UserService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EspaceCoworkingServiceImpl implements EspaceCoworkingService {
    private final EspaceCoworkingRepository espaceCoworkingRepository;
    private final EspaceCoworkingMapper espaceCoworkingMapper;
    private final ProprietaireRepository proprietaireRepository;
    private final UserService userService;

    @Override
    public EspaceCoworkingResponseDTO create(EspaceCoworkingRequestDTO requestDTO) {
        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof Proprietaire)) {
            throw new AccessDeniedException("Seuls les propriétaires peuvent créer des espaces");
        }

        Proprietaire proprietaire = (Proprietaire) currentUser;
        EspaceCoworking espaceCoworking = espaceCoworkingMapper.toEntity(requestDTO);
        espaceCoworking.setProprietaire(proprietaire);
        espaceCoworking.setActive(true);

        validateEspaceCoworking(espaceCoworking);

        return espaceCoworkingMapper.toResponseDTO(espaceCoworkingRepository.save(espaceCoworking));
    }

    @Override
    public EspaceCoworkingResponseDTO update(Long id, EspaceCoworkingRequestDTO requestDTO) {
        EspaceCoworking existingEspace = espaceCoworkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espace coworking non trouvé avec l'id: " + id));

        checkUserAccess(existingEspace);

        existingEspace.setNom(requestDTO.getNom());
        existingEspace.setAdresse(requestDTO.getAdresse());
        existingEspace.setDescription(requestDTO.getDescription());
        existingEspace.setPrixParJour(requestDTO.getPrixParJour());
        existingEspace.setCapacite(requestDTO.getCapacite());

        validateEspaceCoworking(existingEspace);

        return espaceCoworkingMapper.toResponseDTO(espaceCoworkingRepository.save(existingEspace));
    }

    @Override
    public void delete(Long id) {
        EspaceCoworking espace = espaceCoworkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espace coworking non trouvé avec l'id: " + id));

        checkUserAccess(espace);

        espace.setActive(false);
        espaceCoworkingRepository.save(espace);
    }

    @Override
    @Transactional(readOnly = true)
    public EspaceCoworkingResponseDTO getById(Long id) {
        return espaceCoworkingRepository.findByIdAndActiveTrue(id)
                .map(espaceCoworkingMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Espace coworking non trouvé avec l'id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EspaceCoworkingResponseDTO> getAll(Pageable pageable) {
        return espaceCoworkingRepository.findAllByActiveTrue(pageable)
                .map(espaceCoworkingMapper::toResponseDTO);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<EspaceCoworkingResponseDTO> getAllByProprietaire(Long proprietaireId, Pageable pageable) {
        return espaceCoworkingRepository.findByProprietaireIdAndActiveTrue(proprietaireId, pageable)
                .map(espaceCoworkingMapper::toResponseDTO);
    }

    private void checkUserAccess(EspaceCoworking espace) {
        User currentUser = userService.getCurrentUser();
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()));
        boolean isOwner = espace.getProprietaire().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Vous n'avez pas les droits pour modifier cet espace");
        }
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

