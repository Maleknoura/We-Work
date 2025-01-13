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
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Abonnement> abonnements = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Avis> avis = new ArrayList<>();
}
