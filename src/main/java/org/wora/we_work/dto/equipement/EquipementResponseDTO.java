package org.wora.we_work.dto.equipement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public record EquipementResponseDTO(
        Long id,
        Long espaceId,
        String espaceNom,
        String nom,
        String description,
        BigDecimal prix,
        Integer quantite,
        boolean disponible
) {}