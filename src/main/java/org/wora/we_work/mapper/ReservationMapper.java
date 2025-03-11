package org.wora.we_work.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.dto.reservation.ReservationRequest;
import org.wora.we_work.dto.reservation.ReservationResponse;
import org.wora.we_work.entities.Equipement;
import org.wora.we_work.entities.EspaceCoworking;
import org.wora.we_work.entities.Reservation;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "espace.nom", target = "espaceNom")
    @Mapping(source = "dateDebut", target = "dateDebut")
    @Mapping(source = "dateFin", target = "dateFin")
    @Mapping(source = "prixTotal", target = "prixTotal")
    @Mapping(source = "statut", target = "statut")
    @Mapping(source = "nombrePersonnes", target = "nombrePersonnes")
    ReservationResponse toResponse(Reservation reservation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "espace", ignore = true)
    @Mapping(target = "paiement", ignore = true)
    @Mapping(source = "dateDebut", target = "dateDebut")
    @Mapping(source = "dateFin", target = "dateFin")
    @Mapping(source = "nombrePersonnes", target = "nombrePersonnes")
    @Mapping(target = "statut", constant = "EN_ATTENTE")
    Reservation toEntity(ReservationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "espace", ignore = true)
    @Mapping(target = "prixTotal", ignore = true)
    @Mapping(target = "paiement", ignore = true)
    @Mapping(target = "fraisAnnulation", ignore = true)
    @Mapping(target = "dateAnnulation", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(ReservationRequest request, @MappingTarget Reservation reservation);
    EspaceCoworking espaceDtoToEntity(EspaceCoworkingResponseDTO dto);


}




