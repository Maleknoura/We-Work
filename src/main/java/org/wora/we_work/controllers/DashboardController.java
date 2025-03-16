package org.wora.we_work.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.wora.we_work.dto.Stats;
import org.wora.we_work.dto.reservation.ReservationChartData;
import org.wora.we_work.services.impl.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Stats> getDashboardStats(@PathVariable Long userId) {
        Stats stats = dashboardService.getDashboardStats(userId);
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/{userId}/reservation-chart")
    public ResponseEntity<ReservationChartData> getReservationChartData(@PathVariable Long userId) {
        ReservationChartData chartData = dashboardService.getReservationChartData(userId);
        return ResponseEntity.ok(chartData);
    }
}

