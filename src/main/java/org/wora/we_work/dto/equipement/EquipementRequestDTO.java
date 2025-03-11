package org.wora.we_work.dto.equipement;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


public record EquipementRequestDTO(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "La description est obligatoire")
        String description,

        @NotNull(message = "La quantité est obligatoire")
        @Min(value = 0, message = "La quantité doit être positive")
        Integer quantite,

        @NotNull(message = "Le prix est obligatoire")
        @DecimalMin(value = "0.0", inclusive = true, message = "Le prix doit être positif")
        BigDecimal prix,

        @NotNull(message = "L'ID de l'espace de coworking est obligatoire")
        Long espaceCoworkingId

) {}



