package org.wora.we_work.services.api;

import org.wora.we_work.dto.reservation.ReservationChartData;
import org.wora.we_work.dto.statistiques.Stats;

public interface DashboardService {
    Stats getDashboardStats(Long userId);
    ReservationChartData getReservationChartData(Long ownerId);
}
