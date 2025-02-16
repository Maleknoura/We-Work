package org.wora.we_work.dto.espaceCoworking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wora.we_work.dto.UserDTO;
import org.wora.we_work.dto.equipement.EquipementResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

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
     private List<EquipementResponseDTO> equipements;
     private Double notesMoyenne;
     private Integer nombreAvis;
}
