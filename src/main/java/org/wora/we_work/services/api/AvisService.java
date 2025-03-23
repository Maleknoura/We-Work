package org.wora.we_work.services.api;

import org.wora.we_work.dto.avis.AvisRequestDto;
import org.wora.we_work.dto.avis.AvisResponseDto;

import java.util.List;

public interface AvisService {
    AvisResponseDto createAvis(AvisRequestDto request);

    List<AvisResponseDto> getAvisByCoworkingSpace(Long espaceCoworkingId);

    Double getAverageRating(Long espaceCoworkingId);
}

