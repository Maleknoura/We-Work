package org.wora.we_work.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.wora.we_work.dto.paiement.PaiementDTO;
import org.wora.we_work.entities.Paiement;

@Mapper(componentModel = "spring")
public interface PaiementMapper {
    @Mapping(source = "reservation.id", target = "reservationId")
    PaiementDTO toDTO(Paiement paiement);

    @Mapping(source = "reservationId", target = "reservation.id")
    Paiement toEntity(PaiementDTO paiementDTO);
}
