package com.travelmate.flight_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Compagnie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomCompagnie;
    private String codeCompagnie;
    private String paysCompagnie;

    @OneToMany(mappedBy = "compagnie")
    private List<Vol> vols = new ArrayList<>();
}