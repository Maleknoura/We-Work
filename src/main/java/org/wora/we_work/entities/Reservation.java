package org.wora.we_work.entities;

import jakarta.persistence.*;
import lombok.*;
import org.wora.we_work.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

    @Entity
    @Table(name = "reservations")
    @Getter
    @Setter
    @NoArgsConstructor
    public class Reservation {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private LocalDateTime dateDebut;

        @Column(nullable = false)
        private LocalDateTime dateFin;

        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private Status statut;

        private BigDecimal fraisAnnulation;
        private LocalDateTime dateAnnulation;


        @Column(nullable = false)
        private BigDecimal prixTotal;

        @Column(nullable = false)
        private Integer nombrePersonnes;

        @Getter
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "espace_id", nullable = false)
        private EspaceCoworking espace;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
        private Paiement paiement;



    }
