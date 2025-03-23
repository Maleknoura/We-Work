package org.wora.we_work.services.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.wora.we_work.dto.equipement.EquipementRequestDTO;
import org.wora.we_work.dto.equipement.EquipementResponseDTO;
import org.wora.we_work.entities.Equipement;
import org.wora.we_work.entities.Reservation;

import java.math.BigDecimal;
import java.util.List;

public interface EquipementService {
    EquipementResponseDTO create(EquipementRequestDTO requestDTO);

    EquipementResponseDTO update(Long id, EquipementRequestDTO requestDTO);

    void delete(Long id);

    EquipementResponseDTO getById(Long id);

    Page<EquipementResponseDTO> getAll(Pageable pageable);

    Page<EquipementResponseDTO> getAllByEspaceCoworking(Long espaceCoworkingId, Pageable pageable);

    List<EquipementResponseDTO> getEquipementsByIds(List<Long> equipementIds);

    BigDecimal calculerPrixEquipements(List<Long> equipementIds, int nombrePersonnes);

    void verifierDisponibiliteEquipements(List<Long> equipementIds);

    List<EquipementResponseDTO> getAllEquipementsByUserId(Long userId);
}
