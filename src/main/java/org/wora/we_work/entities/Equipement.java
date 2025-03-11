package org.wora.we_work.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "equipements")
    public class Equipement {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "espace_id")
        private EspaceCoworking espace;

        @Column(nullable = false)
        private String nom;

        private String description;

        @Column(nullable = false)
        private Integer quantite;

        @Column(nullable = false)
        private BigDecimal prix;


    }

