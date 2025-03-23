package org.wora.we_work.services.api;

import org.wora.we_work.dto.statistiques.AdminStatsDTO;

public interface AdminService {
    AdminStatsDTO getAdminDashboardStats();
}
