package com.project.uber.uberApp.dto;


import com.project.uber.uberApp.Entities.Rider;
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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestDto {

    private Long id;

    //If we want to get some Data from controllers so we have to provide Jackson with enough info. so it can Serialize/De-Serialize
    //the Data.So how this point will be Serialized/De-Serialized , since this point doesn't have any Jackson specified so we
    //have specify our own implementation using which Jackson can Serialize/De-Serialize b/w this Point Data.We have two options ,
    //1st-> Use this point and define Serialization option for Jackson . 2nd-> Is to use another Entity so will create a "PointDto" ,
    // so rename below Point to "PointDto".
    private PointDto pickupLocation;

    private PointDto dropOffLocation;

    private LocalDate requestedTime;

    private RiderDto rider;

    private Double fare;

    private PaymentMethod paymentMethod;

    private RideRequestStatus rideRequestStatus;
}
