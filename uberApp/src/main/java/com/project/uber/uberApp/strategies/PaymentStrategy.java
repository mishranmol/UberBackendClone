package com.project.uber.uberApp.strategies;

import com.project.uber.uberApp.Entities.Payment;


public interface PaymentStrategy {

    Double PLATFORM_COMMISSION = 0.3; //We can keep this inside our env.variables as well.
    void processPayment(Payment payment);
}
