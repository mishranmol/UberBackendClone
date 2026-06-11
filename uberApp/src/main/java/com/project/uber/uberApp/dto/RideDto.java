package com.project.uber.uberApp.dto;


import com.project.uber.uberApp.Entities.enums.PaymentMethod;
import com.project.uber.uberApp.Entities.enums.RideStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.time.LocalDateTime;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideDto {

    //DTO-> Data Transfer object , used to transfer object b/t different layers so we don't need Jakarta Persistence(JPA)
    // related annotations in DTO , so copied the entire Ride entity and pasted here.
    private Long id;

    private PointDto pickupLocation;

    private PointDto dropOffLocation;

    private LocalDate createdTime;

    private RiderDto rider;

    private DriverDto driver;

    private PaymentMethod paymentMethod;

    private RideStatus rideStatus;

    private String otp;

    private Double fare;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

}
