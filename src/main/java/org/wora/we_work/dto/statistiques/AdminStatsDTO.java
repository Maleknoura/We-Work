package org.wora.we_work.dto.statistiques;

public record AdminStatsDTO(
        long totalProprietaires,
        long totalEspacesCoworking,
        long totalReservations,
        long totalClients
) {}
