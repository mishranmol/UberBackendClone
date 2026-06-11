package com.project.uber.uberApp.Entities;

import com.project.uber.uberApp.Entities.enums.PaymentMethod;
import com.project.uber.uberApp.Entities.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) //Each Payment has a PaymentMethod -> CASH,WALLET
    private PaymentMethod paymentMethod;

    @OneToOne(fetch = FetchType.LAZY) //Since every Ride will have a payment option.
    private Ride ride;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @CreationTimestamp
    private LocalDateTime paymentTime; //Indicates the time when payment was made


}
