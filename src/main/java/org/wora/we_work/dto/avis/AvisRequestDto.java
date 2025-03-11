package org.wora.we_work.dto.avis;

public record AvisRequestDto(Long userId, Long espaceCoworkingId, Integer stars, String comment) {}

