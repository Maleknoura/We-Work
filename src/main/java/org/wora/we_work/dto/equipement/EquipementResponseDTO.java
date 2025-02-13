package org.wora.we_work.dto.equipement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipementResponseDTO {
    private Long id;
    private String nom;
    private String description;
    private Integer quantite;
    private Long espaceCoworkingId;
    private String espaceCoworkingNom;
}
