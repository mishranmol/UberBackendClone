package com.project.uber.uberApp.services;

import com.project.uber.uberApp.Entities.WalletTransaction;
import com.project.uber.uberApp.dto.WalletTransactionDto;

public interface WalletTransactionService {

    void createNewWalletTransaction(WalletTransaction walletTransaction);

}
