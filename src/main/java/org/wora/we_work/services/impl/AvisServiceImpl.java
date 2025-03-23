package org.wora.we_work.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wora.we_work.dto.avis.AvisRequestDto;
import org.wora.we_work.dto.avis.AvisResponseDto;
import org.wora.we_work.entities.Avis;
import org.wora.we_work.mapper.AvisMapper;
import org.wora.we_work.repository.AvisRepository;
import org.wora.we_work.services.api.AvisService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AvisServiceImpl implements AvisService {

    private final AvisRepository avisRepository;
    private final AvisMapper avisMapper;

    @Override
    public AvisResponseDto createAvis(AvisRequestDto request) {
        Avis avis = avisMapper.toEntity(request);
        avis.setDateAvis(LocalDateTime.now());
        Avis savedAvis = avisRepository.save(avis);
        return avisMapper.toDto(savedAvis);
    }

    @Override
    public List<AvisResponseDto> getAvisByCoworkingSpace(Long espaceCoworkingId) {
        return avisRepository.findByEspaceCoworkingId(espaceCoworkingId).stream().map(avisMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Double getAverageRating(Long espaceCoworkingId) {
        return avisRepository.getAverageRatingByCoworkingSpaceId(espaceCoworkingId);
    }
}

