package com.project.uber.uberApp.services;

import com.project.uber.uberApp.Entities.Payment;
import com.project.uber.uberApp.Entities.Ride;
import com.project.uber.uberApp.Entities.enums.PaymentStatus;

public interface PaymentService {

    Payment createNewPayment(Ride ride);


    void processPayment(Ride ride);

    void updatePaymentStatus(Payment payment , PaymentStatus paymentStatus);

}
