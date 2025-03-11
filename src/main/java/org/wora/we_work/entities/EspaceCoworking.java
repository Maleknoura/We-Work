package org.wora.we_work.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "espaces_coworking")
public class EspaceCoworking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "espace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Equipement> equipements = new ArrayList<>();

    @OneToMany(mappedBy = "espace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "espaceCoworking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Avis> avis = new ArrayList<>();

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

    @ElementCollection
    @CollectionTable(name = "espace_images", joinColumns = @JoinColumn(name = "espace_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;

    public BigDecimal getPrixParHeure() {
        return BigDecimal.valueOf(prixParJour).divide(BigDecimal.valueOf(24), RoundingMode.HALF_UP);
    }

    public int getCapaciteMax() {
        return this.capacite;
    }

}
