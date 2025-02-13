package org.wora.we_work.dto.reservation;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservationRequest {
    @NotNull(message = "L'identifiant de l'espace est obligatoire")
    private Long espaceId;

    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être dans le futur")
    private LocalDateTime dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Future(message = "La date de fin doit être dans le futur")
    private LocalDateTime dateFin;

    @NotEmpty(message = "La liste des équipements ne peut pas être vide")
    private List<Long> equipementIds;

    @Min(value = 1, message = "Le nombre de personnes doit être supérieur à 0")
    private int nombrePersonnes;
}
