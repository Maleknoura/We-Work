package org.wora.we_work.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JoinColumn(name = "espace_coworking_id", nullable = false)
    private EspaceCoworking espaceCoworking;

    private String nom;
    private String description;
    private Integer quantite;
}
