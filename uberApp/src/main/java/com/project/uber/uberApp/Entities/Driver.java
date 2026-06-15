package com.project.uber.uberApp.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne//Every Driver is a User . Every Rider has exactly one User account.
    @JoinColumn(name="user_id")//Means Driver table has field named "user_id" and this field will be ForeignKey for User table .
    private User user;

    //Each Driver has a rating
    private Double rating;

    //Whether Driver is available to take ride or not , if available then only assign ride to him
    private Boolean available;

    private String vehicleId;

    //Note: Point should import from jts.geom
    @Column(columnDefinition = "Geometry(Point,4326)") //"Store this field as a geographic Point (latitude/longitude)."
        private Point currentLocation; //columnDefinition = "Geometry(Point,4326)" tells the database to create this column as a geospatial Point type
    // else Hibernate won't be able to guess what's this column for. 4326 indicates the Earth Lati/Longi.

}
