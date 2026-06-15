package com.project.uber.uberApp.services;

import com.project.uber.uberApp.Entities.Ride;
import com.project.uber.uberApp.Entities.User;
import com.project.uber.uberApp.Entities.Wallet;
import com.project.uber.uberApp.Entities.enums.TransactionMethod;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;

public interface WalletService {

    Wallet addMoneyToWallet(User user, Double amount, String transactionId,
                            Ride ride, TransactionMethod transactionMethod);

    Wallet deductMoneyFromWallet(User user, Double amount, String transactionId,
                                 Ride ride, TransactionMethod transactionMethod);

    //This method can be called by Driver only.
    void withdrawAllMyMoneyFromWallet();

    Wallet findWalletById(Long walletId) throws ResourceNotFoundException;

    Wallet createNewWallet(User user);

    Wallet findByUser(User user) throws ResourceNotFoundException;

}
