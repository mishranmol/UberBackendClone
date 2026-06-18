package com.project.uber.uberApp.services.implementations;

import com.project.uber.uberApp.Entities.Ride;
import com.project.uber.uberApp.Entities.User;
import com.project.uber.uberApp.Entities.Wallet;
import com.project.uber.uberApp.Entities.WalletTransaction;
import com.project.uber.uberApp.Entities.enums.TransactionMethod;
import com.project.uber.uberApp.Entities.enums.TransactionType;
import com.project.uber.uberApp.dto.RideDto;
import com.project.uber.uberApp.dto.WalletDto;
import com.project.uber.uberApp.dto.WalletTransactionDto;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.UserRepository;
import com.project.uber.uberApp.repositories.WalletRepository;
import com.project.uber.uberApp.services.WalletService;
import com.project.uber.uberApp.services.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionService walletTransactionService;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public Wallet addMoneyToWallet(User user, Double amount,
                                   String transactionId, Ride ride, TransactionMethod transactionMethod) {

        Wallet wallet = findByUser(user);

       Double newAmount =  wallet.getBalance() + amount ;
       wallet.setBalance(newAmount);


        WalletTransaction walletTransaction = WalletTransaction
                .builder()
                .transactionId(transactionId)
                .ride(ride)
                .wallet(wallet)
                .transactionType(TransactionType.CREDIT)
                .transactionMethod(transactionMethod)
                .amount(amount)
                .build();


        wallet.getTransactions().add(walletTransaction);

       return walletRepository.save(wallet);

    }

    @Override
    @Transactional
    public Wallet deductMoneyFromWallet(User user, Double amount, String transactionId,
                                        Ride ride, TransactionMethod transactionMethod) {

        Wallet wallet = findByUser(user);

        Double left_amount = wallet.getBalance()-amount;
        wallet.setBalance(left_amount);

        WalletTransaction walletTransaction = WalletTransaction
                .builder()
                .transactionId(transactionId)
                .ride(ride)
                .wallet(wallet)
                .transactionType(TransactionType.DEBIT)
                .transactionMethod(transactionMethod)
                .amount(amount)
                .build();

        walletTransactionService.createNewWalletTransaction(walletTransaction); // This was giving some DB error.
        wallet.getTransactions().add(walletTransaction);

        return walletRepository.save(wallet);
    }


    @Override
    public void withdrawAllMyMoneyFromWallet() {
//        TODO -> To be implemented
    }

    @Override
    public Wallet findWalletById(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet with given Id does not exists: "+walletId));
    }

    @Override
    public Wallet createNewWallet(User user) {
         Wallet wallet = new Wallet();
         wallet.setUser(user);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findByUser(User user)  {
        return walletRepository.findByUser(user).orElseThrow(
                () -> new ResourceNotFoundException("Wallet for this user does not exist: "+user.getId())
        );
    }
}
