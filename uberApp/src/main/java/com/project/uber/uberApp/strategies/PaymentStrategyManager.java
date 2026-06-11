package com.project.uber.uberApp.strategies;


import com.project.uber.uberApp.Entities.enums.PaymentMethod;
import com.project.uber.uberApp.strategies.impl.CashPaymentStrategy;
import com.project.uber.uberApp.strategies.impl.WalletPaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStrategyManager {

    private final CashPaymentStrategy cashPaymentStrategy;
    private final WalletPaymentStrategy walletPaymentStrategy;

    public PaymentStrategy paymentStrategy(PaymentMethod paymentMethod){
        switch (paymentMethod){
            case WALLET -> {
                return walletPaymentStrategy;
            }
            case CASH -> {
                return cashPaymentStrategy;
            }
            default -> throw new RuntimeException("Invalid Payment Method: "+paymentMethod);
        }
    }

}
