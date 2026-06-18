package com.project.uber.uberApp.strategies.impl;

import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.Entities.Payment;
import com.project.uber.uberApp.Entities.Rider;
import com.project.uber.uberApp.Entities.Wallet;
import com.project.uber.uberApp.Entities.enums.PaymentStatus;
import com.project.uber.uberApp.Entities.enums.TransactionMethod;
import com.project.uber.uberApp.repositories.PaymentRepository;
import com.project.uber.uberApp.services.PaymentService;
import com.project.uber.uberApp.services.WalletService;
import com.project.uber.uberApp.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



//Rider has 232 , Driver has 500
//Ride cost is 100  , commission = 30
//Rider -> 232-100
//Driver -> 500+ (100-30) = 570

@Service
@RequiredArgsConstructor
public class WalletPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    //Written the Whole Code by own
    @Override
    public void processPayment(Payment payment) {

        Driver driver = payment.getRide().getDriver();
        Rider rider = payment.getRide().getRider();

        Double commission = payment.getAmount() * PLATFORM_COMMISSION;

        Wallet wallet_1 = walletService.deductMoneyFromWallet(rider.getUser(),
                payment.getAmount(),
                null,
                payment.getRide(),
                TransactionMethod.RIDE
        );

        Wallet wallet_2 = walletService.addMoneyToWallet(driver.getUser(),
                payment.getAmount() - commission,
                null,
                payment.getRide(),
                TransactionMethod.RIDE
        );

         payment.setPaymentStatus(PaymentStatus.CONFIRMED);
         paymentRepository.save(payment);

    }
}
