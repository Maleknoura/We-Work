package org.wora.we_work.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingRequestDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.entities.EspaceCoworking;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EspaceCoworkingMapper {
    EspaceCoworkingResponseDTO toResponseDTO(EspaceCoworking espaceCoworking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proprietaire", ignore = true)
    @Mapping(target = "active", constant = "true")
    EspaceCoworking toEntity(EspaceCoworkingRequestDTO requestDTO);

    List<EspaceCoworkingResponseDTO> toResponseDTOList(List<EspaceCoworking> espaceCoworkings);
}
