package org.wora.we_work.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wora.we_work.dto.statistiques.AdminStatsDTO;
import org.wora.we_work.repository.EspaceCoworkingRepository;
import org.wora.we_work.repository.ReservationRepository;
import org.wora.we_work.repository.UserRepository;
import org.wora.we_work.services.api.AdminService;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final EspaceCoworkingRepository espaceCoworkingRepository;
    private final ReservationRepository reservationRepository;


    @Override
    @Transactional(readOnly = true)
    public AdminStatsDTO getAdminDashboardStats() {
        long totalProprietaires = userRepository.countUsersWithRole("ROLE_PROPRIETAIRE");
        long totalClients = userRepository.countUsersWithRole("ROLE_CLIENT");
        long totalEspacesCoworking = espaceCoworkingRepository.count();
        long totalReservations = reservationRepository.count();

        return new AdminStatsDTO(
                totalProprietaires,
                totalEspacesCoworking,
                totalReservations,
                totalClients
        );
    }
}
