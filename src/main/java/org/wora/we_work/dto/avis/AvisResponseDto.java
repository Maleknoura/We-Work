package org.wora.we_work.dto.avis;

import java.time.LocalDateTime;

public record AvisResponseDto(Long id, Long userId, Long espaceCoworkingId, Integer stars, String comment, LocalDateTime dateAvis) {}

