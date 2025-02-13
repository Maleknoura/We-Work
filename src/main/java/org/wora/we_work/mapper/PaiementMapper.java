package org.wora.we_work.mapper;

import org.mapstruct.Mapper;
import org.wora.we_work.dto.PaiementDTO;
import org.wora.we_work.entities.Paiement;

@Mapper(componentModel = "spring")
public interface PaiementMapper {
    PaiementDTO toDto(Paiement paiement);
}
