package org.wora.we_work.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "proprietaires")
@PrimaryKeyJoinColumn(name = "user_id")
public class Proprietaire extends User {
    @Column(name = "total_amount")
    private Double totalAmount = 0.0;

    @Column(name = "Company_name")
    private String CompanyName;

    private String phoneNumber;
    private String siretNumber;

    @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL)
    private List<EspaceCoworking> espacesCoworking = new ArrayList<>();
}


