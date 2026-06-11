package com.project.uber.uberApp.Entities;


import com.project.uber.uberApp.Entities.enums.PaymentMethod;
import com.project.uber.uberApp.Entities.enums.RideRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//Rider will hit the RideRequest
public class RideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "Geometry(Point,4326)")
    private Point pickupLocation;//It will be provided by FrontEnd only,as using Mobile app so googleMap can also generate
    // the co-ordinates and pass them to backend

    @Column(columnDefinition = "Geometry(Point,4326)")
    private Point dropOffLocation;

    @CreationTimestamp //It automatically stores the creation timestamp of an entity when it is first persisted in the database.
    // It eliminates the need to manually set the creation date or time.
    private LocalDate requestedTime;

    @ManyToOne(fetch = FetchType.LAZY) // Means 1 rider can have multiple RideRequest.
    //Default FetchType=EAGER but setting LAZY means when we're fetching the RideRequest then it will not fetch the Rider
    private Rider rider;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private RideRequestStatus rideRequestStatus;

    private Double fare;

}
