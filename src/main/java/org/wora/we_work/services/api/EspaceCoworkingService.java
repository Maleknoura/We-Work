package org.wora.we_work.services.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingRequestDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;

import java.util.List;

public interface EspaceCoworkingService {
    EspaceCoworkingResponseDTO create(EspaceCoworkingRequestDTO requestDTO);
    EspaceCoworkingResponseDTO update(Long id, EspaceCoworkingRequestDTO requestDTO);
    void delete(Long id);
    EspaceCoworkingResponseDTO getById(Long id);
    Page<EspaceCoworkingResponseDTO> getAll(Pageable pageable);
    Page<EspaceCoworkingResponseDTO> getAllByProprietaire(Long proprietaireId, Pageable pageable);
}

