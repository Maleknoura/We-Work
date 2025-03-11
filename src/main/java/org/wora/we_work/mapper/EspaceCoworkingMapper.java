package org.wora.we_work.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingRequestDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.entities.Equipement;
import org.wora.we_work.entities.EspaceCoworking;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//package org.wora.we_work.mapper;
//
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.MappingTarget;
//import org.mapstruct.ReportingPolicy;
//import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingRequestDTO;
//import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
//import org.wora.we_work.entities.Equipement;
//import org.wora.we_work.entities.EspaceCoworking;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
////@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
////public interface EspaceCoworkingMapper {
////    @Mapping(target = "images", source = "images")
////    @Mapping(target = "equipements", ignore = true)
////    EspaceCoworkingResponseDTO toResponseDTO(EspaceCoworking espaceCoworking);
////
////    @Mapping(target = "images", source = "images")
////    @Mapping(target = "id", ignore = true)
////    @Mapping(target = "active", constant = "true")
////    @Mapping(target = "equipements", ignore = true)
////    EspaceCoworking toEntity(EspaceCoworkingRequestDTO requestDTO);
////
////    List<EspaceCoworkingResponseDTO> toResponseDTOList(List<EspaceCoworking> espaceCoworkings);
////
////}
@Mapper(componentModel = "spring")
public interface EspaceCoworkingMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "equipements", target = "equipementsIds", qualifiedByName = "equipementsToIds")
    @Mapping(source = "nom", target = "nom")
    @Mapping(source = "adresse", target = "adresse")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "prixParJour", target = "prixParJour")
    @Mapping(source = "capacite", target = "capacite")
    @Mapping(source = "images", target = "images")
    @Mapping(source = "active", target = "active")
    EspaceCoworkingResponseDTO toResponseDTO(EspaceCoworking espaceCoworking);

    @Named("equipementsToIds")
    default List<Long> equipementsToIds(List<Equipement> equipements) {
        if (equipements == null) {
            return Collections.emptyList();
        }
        return equipements.stream()
                .map(Equipement::getId)
                .collect(Collectors.toList());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "equipements", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "avis", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateModification", ignore = true)
    @Mapping(source = "nom", target = "nom")
    @Mapping(source = "adresse", target = "adresse")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "prixParJour", target = "prixParJour")
    @Mapping(source = "capacite", target = "capacite")
    @Mapping(source = "images", target = "images")
    @Mapping(target = "active", expression = "java(true)")
    EspaceCoworking toEntity(EspaceCoworkingRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "equipements", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "avis", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateModification", ignore = true)
    void updateEntityFromDTO(EspaceCoworkingRequestDTO requestDTO, @MappingTarget EspaceCoworking existingEspace);   List<EspaceCoworkingResponseDTO> toResponseDTOList(List<EspaceCoworking> espaceCoworkings);

}

