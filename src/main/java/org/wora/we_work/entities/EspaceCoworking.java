package org.wora.we_work.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "espaces_coworking")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EspaceCoworking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "espaceCoworking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Equipement> equipements = new ArrayList<>();

    @OneToMany(mappedBy = "espaceCoworking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Avis> avis = new ArrayList<>();

    @OneToMany(mappedBy = "espace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();


    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private Double prixParJour;

    @Column(nullable = false)
    private Integer capacite;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;

    public BigDecimal getPrixParHeure() {
        return BigDecimal.valueOf(prixParJour / 24.0);
    }

    public int getCapaciteMax() {
        return this.capacite;
    }
}