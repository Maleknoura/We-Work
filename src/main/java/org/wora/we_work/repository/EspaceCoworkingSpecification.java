package org.wora.we_work.repository;

import jakarta.persistence.criteria.Join;


import org.wora.we_work.entities.EspaceCoworking;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;


public class EspaceCoworkingSpecification {

    public static Specification<EspaceCoworking> hasPriceEqual(BigDecimal price) {
        return (root, query, criteriaBuilder) -> {
            if (price != null) {
                return criteriaBuilder.equal(root.get("prixParJour"), price);
            }
            return null;
        };
    }

    public static Specification<EspaceCoworking> hasCapacityEqual(Integer capacity) {
        return (root, query, criteriaBuilder) -> {
            if (capacity != null) {
                return criteriaBuilder.equal(root.get("capacite"), capacity);
            }
            return null;
        };
    }

    public static Specification<EspaceCoworking> hasAddress(String address) {
        return (root, query, criteriaBuilder) -> {
            if (address != null && !address.isEmpty()) {
                return criteriaBuilder.like(root.get("adresse"), "%" + address + "%");
            }
            return null;
        };
    }

    public static Specification<EspaceCoworking> hasEquipements(List<String> equipementNames) {
        return (root, query, criteriaBuilder) -> {
            if (equipementNames == null || equipementNames.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            Join<Object, Object> equipementJoin = root.join("equipements");

            return equipementJoin.get("nom").in(equipementNames);
        };
    }

}


