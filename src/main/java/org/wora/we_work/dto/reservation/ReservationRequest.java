package org.wora.we_work.dto.reservation;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationRequest(@NotNull(message = "L'identifiant de l'espace est obligatoire") Long espaceId,

                                 @NotNull(message = "La date de début est obligatoire") @Future(message = "La date de début doit être dans le futur") LocalDateTime dateDebut,

                                 @NotNull(message = "La date de fin est obligatoire") @Future(message = "La date de fin doit être dans le futur") LocalDateTime dateFin,

                                 List<Long> equipementIds,

                                 @Min(value = 1, message = "Le nombre de personnes doit être supérieur à 0") int nombrePersonnes) {
}
