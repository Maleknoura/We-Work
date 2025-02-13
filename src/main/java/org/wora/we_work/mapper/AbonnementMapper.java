package org.wora.we_work.mapper;

import org.mapstruct.Mapper;
import org.wora.we_work.dto.AbonnementDTO;
import org.wora.we_work.entities.Abonnement;

@Mapper(componentModel = "spring")
public interface AbonnementMapper {
    AbonnementDTO toDto(Abonnement abonnement);
}

