package org.wora.we_work.dto.espaceCoworking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public record EspaceCoworkingResponseDTO(
        Long id,
        Long userId,
        List<Long> equipementsIds,
        String nom,
        String adresse,
        String description,
        Double prixParJour,
        Integer capacite,
        List<String> images,
        boolean active,
        LocalDateTime dateCreation,
        LocalDateTime dateModification
) {

}



