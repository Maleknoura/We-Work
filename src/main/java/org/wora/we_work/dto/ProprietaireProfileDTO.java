package org.wora.we_work.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProprietaireProfileDTO {
    @NotBlank(message = "Le nom de l'entreprise est obligatoire")
    private String companyName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Numéro de téléphone invalide")
    private String phoneNumber;

    @Pattern(regexp = "^[0-9A-Z]{10,20}$", message = "Numéro SIRET invalide")
    private String siretNumber;
}
