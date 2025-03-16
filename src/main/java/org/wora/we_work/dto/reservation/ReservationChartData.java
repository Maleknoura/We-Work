package org.wora.we_work.dto.reservation;

import java.util.List;

public record ReservationChartData(List<String> labels, List<Integer> reservations) {
}

