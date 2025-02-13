package org.wora.we_work.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wora.we_work.dto.equipement.EquipementRequestDTO;
import org.wora.we_work.dto.equipement.EquipementResponseDTO;
import org.wora.we_work.entities.Equipement;
import org.wora.we_work.entities.EspaceCoworking;
import org.wora.we_work.entities.User;
import org.wora.we_work.exception.ResourceNotFoundException;
import org.wora.we_work.repository.EquipementRepository;
import org.wora.we_work.repository.EspaceCoworkingRepository;
import org.wora.we_work.services.api.EquipementService;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class EquipementServiceImpl implements EquipementService {
    private final EquipementRepository equipementRepository;
    private final EspaceCoworkingRepository espaceCoworkingRepository;

    @Override
    public EquipementResponseDTO create(EquipementRequestDTO requestDTO) {
        EspaceCoworking espaceCoworking = espaceCoworkingRepository.findById(requestDTO.getEspaceCoworkingId())
                .orElseThrow(() -> new ResourceNotFoundException("Espace de coworking non trouvé"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();



        Equipement equipement = new Equipement();
        equipement.setNom(requestDTO.getNom());
        equipement.setDescription(requestDTO.getDescription());
        equipement.setQuantite(requestDTO.getQuantite());
        equipement.setEspaceCoworking(espaceCoworking);

        equipement = equipementRepository.save(equipement);
        return toDTO(equipement);
    }

    @Override
    public EquipementResponseDTO update(Long id, EquipementRequestDTO requestDTO) {
        Equipement equipement = equipementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Équipement non trouvé"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();


        equipement.setNom(requestDTO.getNom());
        equipement.setDescription(requestDTO.getDescription());
        equipement.setQuantite(requestDTO.getQuantite());

        equipement = equipementRepository.save(equipement);
        return toDTO(equipement);
    }

    @Override
    public void delete(Long id) {
        Equipement equipement = equipementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Équipement non trouvé"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();



        equipementRepository.delete(equipement);
    }

    @Override
    public EquipementResponseDTO getById(Long id) {
        return equipementRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Équipement non trouvé"));
    }

    @Override
    public Page<EquipementResponseDTO> getAll(Pageable pageable) {
        return equipementRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public Page<EquipementResponseDTO> getAllByEspaceCoworking(Long espaceCoworkingId, Pageable pageable) {
        return equipementRepository.findByEspaceCoworkingId(espaceCoworkingId, pageable)
                .map(this::toDTO);
    }

    private EquipementResponseDTO toDTO(Equipement equipement) {
        return EquipementResponseDTO.builder()
                .id(equipement.getId())
                .nom(equipement.getNom())
                .description(equipement.getDescription())
                .quantite(equipement.getQuantite())
                .espaceCoworkingId(equipement.getEspaceCoworking().getId())
                .espaceCoworkingNom(equipement.getEspaceCoworking().getNom())
                .build();
    }
}
