package org.wora.we_work.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingRequestDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingSearchCriteria;
import org.wora.we_work.entities.Equipement;
import org.wora.we_work.entities.EspaceCoworking;
import org.wora.we_work.entities.User;
import org.wora.we_work.exception.ResourceNotFoundException;
import org.wora.we_work.mapper.EspaceCoworkingMapper;
import org.wora.we_work.repository.EquipementRepository;
import org.wora.we_work.repository.EspaceCoworkingRepository;
import org.wora.we_work.repository.EspaceCoworkingSpecification;
import org.wora.we_work.services.api.EspaceCoworkingService;
import org.wora.we_work.services.api.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EspaceCoworkingServiceImpl implements EspaceCoworkingService {

    private final EspaceCoworkingRepository espaceCoworkingRepository;
    private final EspaceCoworkingMapper espaceCoworkingMapper;
    private final UserService userService;

    @Secured("ESPACE_CREATE")
    @Transactional
    @Override
    public EspaceCoworkingResponseDTO create(EspaceCoworkingRequestDTO requestDTO) {
        User currentUser = userService.getCurrentUser();

        EspaceCoworking espaceCoworking = espaceCoworkingMapper.toEntity(requestDTO);
        espaceCoworking.setUser(currentUser);
        espaceCoworking.setActive(true);

        return espaceCoworkingMapper.toResponseDTO(espaceCoworkingRepository.save(espaceCoworking));
    }

    @Secured("ESPACE_UPDATE")
    @Transactional
    @Override
    public EspaceCoworkingResponseDTO update(Long id, EspaceCoworkingRequestDTO requestDTO) {
        EspaceCoworking existingEspace = espaceCoworkingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Espace non trouvé"));

        espaceCoworkingMapper.updateEntityFromDTO(requestDTO, existingEspace);

        return espaceCoworkingMapper.toResponseDTO(espaceCoworkingRepository.save(existingEspace));
    }

    @Override
    public void delete(Long id) {
        EspaceCoworking espace = espaceCoworkingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Espace coworking non trouvé avec l'id: " + id));

        espaceCoworkingRepository.delete(espace);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EspaceCoworkingResponseDTO> getAll(Pageable pageable) {
        return espaceCoworkingRepository.findAll(pageable).map(espaceCoworkingMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public EspaceCoworkingResponseDTO getById(Long id) {
        EspaceCoworking espace = espaceCoworkingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Espace coworking non trouvé avec l'id: " + id));
        return espaceCoworkingMapper.toResponseDTO(espace);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EspaceCoworkingResponseDTO> getAllByProprietaire(Long proprietaireId, Pageable pageable) {
        return espaceCoworkingRepository.findByUserId(proprietaireId, pageable).map(espaceCoworkingMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspaceCoworkingResponseDTO> searchEspaceCoworkings(EspaceCoworkingSearchCriteria criteria) {
        Specification<EspaceCoworking> spec = Specification.where(null);

        if (criteria.getPrixParJour() != null) {
            BigDecimal prix = BigDecimal.valueOf(criteria.getPrixParJour());
            spec = spec.and(EspaceCoworkingSpecification.hasPriceEqual(prix));
        }

        if (criteria.getCapacite() != null) {
            spec = spec.and(EspaceCoworkingSpecification.hasCapacityEqual(criteria.getCapacite()));
        }

        if (criteria.getAdresse() != null && !criteria.getAdresse().isEmpty()) {
            spec = spec.and(EspaceCoworkingSpecification.hasAddress(criteria.getAdresse()));
        }

        if (criteria.getEquipements() != null && !criteria.getEquipements().isEmpty()) {
            spec = spec.and(EspaceCoworkingSpecification.hasEquipements(criteria.getEquipements()));
        }

        List<EspaceCoworking> espaces = espaceCoworkingRepository.findAll(spec);

        return espaceCoworkingMapper.toResponseDTOList(espaces);
    }


    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculerPrixBase(EspaceCoworking espace, int nombrePersonnes, LocalDate dateDebut, LocalDate dateFin) {
        final long nombreJours = ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;

        return BigDecimal.valueOf(espace.getPrixParJour()).multiply(BigDecimal.valueOf(nombrePersonnes)).multiply(BigDecimal.valueOf(nombreJours));
    }

    public EspaceCoworking findById(Long id) {
        return espaceCoworkingRepository.findById(id).orElseThrow(() -> new RuntimeException("espace non trouvé"));
    }
}