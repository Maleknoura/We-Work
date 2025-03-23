package org.wora.we_work.services.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingRequestDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingSearchCriteria;
import org.wora.we_work.entities.EspaceCoworking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface EspaceCoworkingService {

    EspaceCoworkingResponseDTO create(EspaceCoworkingRequestDTO requestDTO);

    EspaceCoworkingResponseDTO update(Long id, EspaceCoworkingRequestDTO requestDTO);

    void delete(Long id);

    Page<EspaceCoworkingResponseDTO> getAll(Pageable pageable);

    EspaceCoworkingResponseDTO getById(Long id);

    Page<EspaceCoworkingResponseDTO> getAllByProprietaire(Long proprietaireId, Pageable pageable);

    List<EspaceCoworkingResponseDTO> searchEspaceCoworkings(EspaceCoworkingSearchCriteria criteria);

    BigDecimal calculerPrixBase(EspaceCoworking espace, int nombrePersonnes, LocalDate dateDebut, LocalDate dateFin);

    EspaceCoworking findById(Long id);
}


