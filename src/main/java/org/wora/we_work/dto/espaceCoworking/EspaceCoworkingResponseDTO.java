package org.wora.we_work.dto.espaceCoworking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wora.we_work.dto.UserDTO;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspaceCoworkingResponseDTO {
    private Long id;
    private String nom;
    private String adresse;
    private String description;
    private Double prixParJour;
    private Integer capacite;
    private boolean active;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private UserDTO user;
//     private List<EquipementDTO> equipements;
     private Double notesMoyenne;
     private Integer nombreAvis;
}
