package com.project.uber.uberApp.strategies.impl;

import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.Entities.Payment;
import com.project.uber.uberApp.Entities.Wallet;
import com.project.uber.uberApp.Entities.enums.PaymentStatus;
import com.project.uber.uberApp.Entities.enums.TransactionMethod;
import com.project.uber.uberApp.repositories.PaymentRepository;
import com.project.uber.uberApp.services.PaymentService;
import com.project.uber.uberApp.services.WalletService;
import com.project.uber.uberApp.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


//IMP -> If Value of Ride is = 100 , so Rider will be paying 100 to Driver but out of 100 we'll be deducting 30 from
// Driver's wallet as PLATFORM_COMMISSION.So this CODStrategy won't require Riders wallet.
@Service
@RequiredArgsConstructor
public class CashPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    @Override
    public void processPayment(Payment payment) {

        Driver driver = payment.getRide().getDriver();

        double platformCommission = payment.getAmount() * PLATFORM_COMMISSION;

        //deducting the commissionMoney from Driver's Wallet.
        //Since COD payment so transactionID=null.
        Wallet wallet = walletService.deductMoneyFromWallet(driver.getUser(),platformCommission,
                null,payment.getRide(),
                TransactionMethod.RIDE);

        //Confirming the paymentStatus after successful payment
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);

        //TODO in future -> We can also implement that if Driver's wallet is Negative so don't allow then to have access to
        // COD Ride or make the driver deactivate till their wallet is not positive.



    }
}
