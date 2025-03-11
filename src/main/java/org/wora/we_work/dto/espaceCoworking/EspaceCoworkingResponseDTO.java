package org.wora.we_work.dto.espaceCoworking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wora.we_work.dto.UserDTO;
import org.wora.we_work.dto.equipement.EquipementDTO;
import org.wora.we_work.dto.equipement.EquipementResponseDTO;
import org.wora.we_work.entities.Equipement;
import org.wora.we_work.entities.EspaceCoworking;

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
) {}



