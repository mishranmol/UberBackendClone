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

    @OneToOne//Every Rider is a User . Every Rider has exactly one User account.
    @JoinColumn(name="user_id")//Means Rider table has field named "user_id" and this field will be ForeignKey for User table .

    private User user;

    //Each Rider has a rating
    private Double rating;

}
