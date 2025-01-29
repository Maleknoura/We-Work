package org.wora.we_work.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "clients")
@PrimaryKeyJoinColumn(name = "user_id")
public class Client extends User {
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Abonnement> abonnements = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Avis> avis = new ArrayList<>();

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;
}

