package org.wora.we_work.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingSearchCriteria;
import org.wora.we_work.entities.EspaceCoworking;
import org.wora.we_work.services.api.EspaceCoworkingService;

import java.util.List;

@RestController
@RequestMapping("/api/espaces-search")
@RequiredArgsConstructor
public class EspaceSearchController {
    private final EspaceCoworkingService espaceCoworkingService;

    @GetMapping
    public ResponseEntity<List<EspaceCoworkingResponseDTO>> searchEspaceCoworkings(
            @RequestParam(required = false) Double prixParJour,
            @RequestParam(required = false) Integer capacite,
            @RequestParam(required = false) String adresse,
            @RequestParam(required = false) List<String> equipements) {

        EspaceCoworkingSearchCriteria criteria = new EspaceCoworkingSearchCriteria();
        criteria.setPrixParJour(prixParJour);
        criteria.setCapacite(capacite);
        criteria.setAdresse(adresse);
        criteria.setEquipements(equipements);

        List<EspaceCoworkingResponseDTO> espaces = espaceCoworkingService.searchEspaceCoworkings(criteria);
        return ResponseEntity.ok(espaces);
    }}
