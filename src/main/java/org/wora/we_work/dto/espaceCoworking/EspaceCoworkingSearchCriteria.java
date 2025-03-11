package org.wora.we_work.dto.espaceCoworking;

import lombok.*;

import java.util.List;


@Getter
@Setter
public class EspaceCoworkingSearchCriteria {
    private Double prixParJour;
    private Integer capacite;
    private String adresse;
    private List<String> equipements;
}

