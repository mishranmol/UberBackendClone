package com.project.uber.uberApp.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

//Rider is Customer
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id")

    private User user;

    //Each Rider has a rating
    private Double rating;

}
