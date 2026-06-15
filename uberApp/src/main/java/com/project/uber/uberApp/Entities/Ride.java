package com.project.uber.uberApp.Entities;

import com.project.uber.uberApp.Entities.enums.PaymentMethod;
import com.project.uber.uberApp.Entities.enums.RideRequestStatus;
import com.project.uber.uberApp.Entities.enums.RideStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//This will be when DRIVER accepts the Ride
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "Geometry(Point,4326)")
    private Point pickupLocation;//It will be provided by FrontEnd only,as using Mobile app so googleMap can also generate
    // the co-ordinates and pass them to backend

    @Column(columnDefinition = "Geometry(Point,4326)")
    private Point dropOffLocation;

    @CreationTimestamp
    //It automatically stores the creation timestamp of an entity when it is first persisted in the database.
    // It eliminates the need to manually set the creation date or time.
    private LocalDate createdTime; // When Driver accepts your Ride

    @ManyToOne(fetch = FetchType.LAZY) //Means 1 rider can have multiple RideRequest.
    // You can understand like-->Many Rides belong to one rider so ManytoOne . Many=Ride , One=Rider
    //Default FetchType=EAGER but setting LAZY means when we're fetching the RideRequest then it will not fetch the Rider
    private Rider rider;

    @ManyToOne(fetch = FetchType.LAZY) // Means 1 driver can have multiple RideRequest.
    //Default FetchType=EAGER but setting LAZY means when we're fetching the RideRequest then it will not fetch the Rider
    private Driver driver;

    @Enumerated(EnumType.STRING)//@Enumerated is used to tell JPA how to store an Enum in the DB . By Default , "EnumType.ORDINAL"
    //like it will store values in form of 0,1,2 but "EnumType.STRING" , it stores the Exact value of enum inside DB and is more preferred
    //than EnumType.ORDINAL.
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private RideStatus rideStatus;

    private String otp;

    private Double fare;
    private LocalDateTime startedAt; //When driver starts your ride
    private LocalDateTime endedAt; //When driver ends your ride


}
