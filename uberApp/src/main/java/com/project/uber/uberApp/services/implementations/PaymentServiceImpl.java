package com.project.uber.uberApp.services.implementations;

import com.project.uber.uberApp.Entities.Payment;
import com.project.uber.uberApp.Entities.Ride;
import com.project.uber.uberApp.Entities.enums.PaymentStatus;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.PaymentRepository;
import com.project.uber.uberApp.services.PaymentService;
import com.project.uber.uberApp.strategies.PaymentStrategy;
import com.project.uber.uberApp.strategies.PaymentStrategyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStrategyManager paymentStrategyManager;

    //In payment, we can have a PaymentStrategy and using that Strategy we can process the payment depending upon paymentMethodType.
    @Override
    public void processPayment(Ride ride) {
        //Payment has Ride, but Ride does not have Payment.
        Payment payment = paymentRepository.findByRide(ride)
                .orElseThrow(() ->  new ResourceNotFoundException("Payment not found for Ride: "+ride.getId()));
      PaymentStrategy paymentStrategy = paymentStrategyManager.paymentStrategy(payment.getPaymentMethod());
      paymentStrategy.processPayment(payment);
    }

    @Override
    public void updatePaymentStatus(Payment payment, PaymentStatus paymentStatus) {
        payment.setPaymentStatus(paymentStatus);
        paymentRepository.save(payment);
    }


    @Override
    public Payment createNewPayment(Ride ride) {
        Payment payment = Payment.builder()
                .ride(ride)
                .amount(ride.getFare())
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(ride.getPaymentMethod())
                .build();
        return paymentRepository.save(payment);
    }

}
