package org.wora.we_work.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wora.we_work.dto.statistiques.Stats;
import org.wora.we_work.dto.reservation.ReservationChartData;
import org.wora.we_work.repository.AvisRepository;
import org.wora.we_work.repository.EquipementRepository;
import org.wora.we_work.repository.EspaceCoworkingRepository;
import org.wora.we_work.repository.ReservationRepository;
import org.wora.we_work.services.api.DashboardService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final EspaceCoworkingRepository espaceCoworkingRepository;
    private final ReservationRepository reservationRepository;
    private final EquipementRepository equipementRepository;
    private final AvisRepository avisRepository;


    @Transactional(readOnly = true)
    @Override
    public Stats getDashboardStats(Long userId) {
        return new Stats(espaceCoworkingRepository.countByUserId(userId), reservationRepository.countByUserId(userId), equipementRepository.countByUserId(userId), avisRepository.countByUserId(userId));
    }

    @Override
    public ReservationChartData getReservationChartData(Long ownerId) {
        List<String> labels = List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        int currentYear = LocalDate.now().getYear();
        List<Integer> monthlyReservations = new ArrayList<>(12);

        for (int month = 1; month <= 12; month++) {
            int reservationCount = reservationRepository.countByOwnerIdAndYearAndMonth(ownerId, currentYear, month);
            monthlyReservations.add(reservationCount);
        }

        return new ReservationChartData(labels, monthlyReservations);
    }

}

