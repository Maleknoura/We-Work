package org.wora.we_work.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wora.we_work.dto.statistiques.AdminStatsDTO;
import org.wora.we_work.services.api.AdminService;

@RestController
@RequestMapping("/api/admin/dashboard")
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;


    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getAdminDashboardStats() {
        AdminStatsDTO stats = adminService.getAdminDashboardStats();
        return ResponseEntity.ok(stats);
    }
}
