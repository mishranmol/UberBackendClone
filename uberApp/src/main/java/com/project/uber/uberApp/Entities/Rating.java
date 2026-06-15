package com.project.uber.uberApp.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne //One rating will belong to One ride
    private Ride ride;


    //Now a question can arise that each Ride contains "Driver" & "Rider" then why we are adding it again here?
    //Ans -> Doing this to calculate the Avg. rating quickly otherwise have to make join table call inside Ride and get the rider.
    //Ex -> If we want to fetch the rating of all the rides of a particular rider , then if Driver/Rider not here then we'll go to
    //each ride and check which ride contains this particular rider . Hence, adding here so that API calls are faster.

    @ManyToOne //One Rider can have many Rating
    private Rider rider;

    @ManyToOne //One Rider can have many Rating
    private Driver driver;

    private Integer driverRating; //rating of Driver for this particular Ride

    private Integer riderRating; //rating of Rider for this particular Ride

}

