package org.wora.we_work.dto.espaceCoworking;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspaceCoworkingRequestDTO {
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String nom;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(min = 5, max = 200, message = "L'adresse doit contenir entre 5 et 200 caractères")
    private String adresse;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, max = 1000, message = "La description doit contenir entre 10 et 1000 caractères")
    private String description;

    @NotNull(message = "Le prix par jour est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private Double prixParJour;

    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 1, message = "La capacité doit être d'au moins 1 personne")
    @Max(value = 1000, message = "La capacité ne peut pas dépasser 1000 personnes")
    private Integer capacite;
}