package org.wora.we_work.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "avis")
public class Avis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "espace_coworking_id", nullable = false)
    private EspaceCoworking espaceCoworking;

    private Integer stars;
    private String comment;
    private LocalDateTime dateAvis;
}
