package org.wora.we_work.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.wora.we_work.dto.reservation.ReservationResponse;
import org.wora.we_work.entities.Equipement;
import org.wora.we_work.entities.Reservation;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class, EspaceCoworkingMapper.class, AbonnementMapper.class, PaiementMapper.class})
public interface ReservationMapper {
    @Mapping(source = "user", target = "user")
    @Mapping(source = "espace", target = "espaceCoworking")
    @Mapping(source = "paiement", target = "paiement")
    ReservationResponse toResponse(Reservation reservation);

    List<ReservationResponse> toResponseList(List<Reservation> reservations);
}

