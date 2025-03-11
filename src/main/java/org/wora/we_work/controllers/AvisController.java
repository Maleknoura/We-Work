package org.wora.we_work.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wora.we_work.dto.avis.AvisRequestDto;
import org.wora.we_work.dto.avis.AvisResponseDto;
import org.wora.we_work.services.api.AvisService;

import java.util.List;

@RestController
@RequestMapping("/api/avis")
public class AvisController {

    private final AvisService avisService;

    public AvisController(AvisService avisService) {
        this.avisService = avisService;
    }

    @PostMapping
    public ResponseEntity<AvisResponseDto> createAvis(@RequestBody AvisRequestDto request) {
        return ResponseEntity.ok(avisService.createAvis(request));
    }

    @GetMapping("/coworking/{espaceCoworkingId}")
    public ResponseEntity<List<AvisResponseDto>> getAvisByCoworkingSpace(@PathVariable Long espaceCoworkingId) {
        return ResponseEntity.ok(avisService.getAvisByCoworkingSpace(espaceCoworkingId));
    }

    @GetMapping("/coworking/{espaceCoworkingId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long espaceCoworkingId) {
        return ResponseEntity.ok(avisService.getAverageRating(espaceCoworkingId));
    }
}

