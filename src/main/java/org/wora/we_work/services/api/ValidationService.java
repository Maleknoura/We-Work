package org.wora.we_work.services.api;

import org.wora.we_work.entities.EspaceCoworking;
import org.wora.we_work.entities.Reservation;

import java.time.LocalDateTime;

public interface ValidationService {
    void verifySpaceAvailability(EspaceCoworking space, LocalDateTime startDate, LocalDateTime endDate);
    void verifyCancellationPossibility(Reservation reservation);
}

