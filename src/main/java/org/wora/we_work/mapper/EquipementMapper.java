package org.wora.we_work.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.wora.we_work.dto.equipement.EquipementRequestDTO;
import org.wora.we_work.dto.equipement.EquipementResponseDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.entities.Equipement;
import org.wora.we_work.entities.EspaceCoworking;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EquipementMapper {

    @Mapping(source = "espace.id", target = "espaceId")
    @Mapping(source = "espace.nom", target = "espaceNom")
    @Mapping(target = "disponible", expression = "java(equipement.getQuantite() > 0)")
    EquipementResponseDTO toDto(Equipement equipement);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "espace", ignore = true)
    @Mapping(source = "prix", target = "prix")
    Equipement toEntity(EquipementRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "espace", ignore = true)
    @Mapping(source = "prix", target = "prix")
    void updateEntityFromDto(EquipementRequestDTO requestDTO, @MappingTarget Equipement equipement);
    List<EquipementResponseDTO> toResponseDTOList(List<Equipement> equipements);

    EspaceCoworking mapEspaceDtoToEntity(EspaceCoworkingResponseDTO espaceDTO);
}