package org.wora.we_work.services.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wora.we_work.dto.equipement.EquipementRequestDTO;
import org.wora.we_work.dto.equipement.EquipementResponseDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.entities.Equipement;
import org.wora.we_work.entities.EspaceCoworking;
import org.wora.we_work.exception.ResourceNotFoundException;
import org.wora.we_work.exception.ResourceUnavailableException;
import org.wora.we_work.mapper.EquipementMapper;
import org.wora.we_work.repository.EquipementRepository;
import org.wora.we_work.services.api.EquipementService;
import org.wora.we_work.services.api.EspaceCoworkingService;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EquipementServiceImpl implements EquipementService {

    private final EquipementRepository equipementRepository;
    private final EquipementMapper equipementMapper;
    private final EspaceCoworkingService espaceCoworkingService;

    @Override
    public EquipementResponseDTO create(EquipementRequestDTO requestDTO) {
        EspaceCoworkingResponseDTO espaceDTO = espaceCoworkingService.getById(requestDTO.espaceCoworkingId());
        EspaceCoworking espace = equipementMapper.mapEspaceDtoToEntity(espaceDTO);

        Equipement equipement = equipementMapper.toEntity(requestDTO);
        equipement.setEspace(espace);

        Equipement savedEquipement = equipementRepository.save(equipement);
        return equipementMapper.toDto(savedEquipement);
    }


    @Override
    public EquipementResponseDTO update(Long id, EquipementRequestDTO requestDTO) {
        Equipement equipement = equipementRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Équipement non trouvé"));

        equipementMapper.updateEntityFromDto(requestDTO, equipement);

        Equipement updatedEquipement = equipementRepository.save(equipement);
        return equipementMapper.toDto(updatedEquipement);
    }

    @Override
    public void delete(Long id) {
        Equipement equipement = equipementRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Équipement non trouvé"));

        equipementRepository.delete(equipement);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipementResponseDTO getById(Long id) {
        Equipement equipement = equipementRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Équipement non trouvé"));
        return equipementMapper.toDto(equipement);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EquipementResponseDTO> getAll(Pageable pageable) {
        return equipementRepository.findAll(pageable).map(equipementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EquipementResponseDTO> getAllByEspaceCoworking(Long espaceCoworkingId, Pageable pageable) {
        return equipementRepository.findByEspaceId(espaceCoworkingId, pageable).map(equipementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipementResponseDTO> getEquipementsByIds(List<Long> equipementIds) {
        List<Equipement> equipements = equipementRepository.findAllById(equipementIds);

        if (equipements.size() != equipementIds.size()) {
            throw new ResourceNotFoundException("Certains équipements demandés n'existent pas");
        }

        return equipementMapper.toResponseDTOList(equipements);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculerPrixEquipements(List<Long> equipementIds, int nombrePersonnes) {
        if (equipementIds == null || equipementIds.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return equipementRepository.findAllById(equipementIds).stream().map(equipement -> equipement.getPrix().multiply(BigDecimal.valueOf(nombrePersonnes))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    @Override
    @Transactional(readOnly = true)
    public void verifierDisponibiliteEquipements(List<Long> equipementIds) {
        if (equipementIds == null || equipementIds.isEmpty()) {
            return;
        }

        List<Equipement> equipements = equipementRepository.findAllById(equipementIds);

        if (equipements.size() != equipementIds.size()) {
            throw new ResourceNotFoundException("Certains équipements demandés n'existent pas");
        }

        for (Equipement equipement : equipements) {
            if (equipement.getQuantite() <= 0) {
                throw new ResourceUnavailableException("L'équipement " + equipement.getNom() + " n'est pas disponible");
            }
        }
    }

    @Override
    public List<EquipementResponseDTO> getAllEquipementsByUserId(Long userId) {
        List<Equipement> equipements = equipementRepository.findAllEquipementsByUserId(userId);
        return equipementMapper.toResponseDTOList(equipements);
    }
}
