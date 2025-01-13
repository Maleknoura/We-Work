package org.wora.we_work.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "espaces_coworking")
public class EspaceCoworking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proprietaire_id", nullable = false)
    private Proprietaire proprietaire;

    @OneToMany(mappedBy = "espaceCoworking", cascade = CascadeType.ALL)
    private List<Equipement> equipements = new ArrayList<>();

    @OneToMany(mappedBy = "espaceCoworking", cascade = CascadeType.ALL)
    private List<Avis> avis = new ArrayList<>();

    private String nom;
    private String adresse;
    private String description;
    private Double prixParJour;
    private Integer capacite;
}
