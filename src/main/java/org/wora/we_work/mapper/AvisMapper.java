package org.wora.we_work.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.wora.we_work.dto.avis.AvisRequestDto;
import org.wora.we_work.dto.avis.AvisResponseDto;
import org.wora.we_work.entities.Avis;

@Mapper(componentModel = "spring")
public interface AvisMapper {
    AvisMapper INSTANCE = Mappers.getMapper(AvisMapper.class);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "espaceCoworking.id", target = "espaceCoworkingId")
    AvisResponseDto toDto(Avis avis);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "espaceCoworkingId", target = "espaceCoworking.id")
    Avis toEntity(AvisRequestDto dto);
}

