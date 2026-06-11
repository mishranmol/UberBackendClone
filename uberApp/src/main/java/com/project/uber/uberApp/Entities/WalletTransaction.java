package com.project.uber.uberApp.Entities;

import com.project.uber.uberApp.Entities.enums.TransactionMethod;
import com.project.uber.uberApp.Entities.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// The Wallet Entity can contain a lot of transaction, like adding funds from bank into the wallet or Debit Transaction in which Driver is
// actually Cashing out the payment from his wallet to his Bank Account , so we have to create WalletTransaction Entity.

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Each Transaction has some Amount
    private Double amount;

    //Each Transaction has some type --> CREDIT,DEBIT
    private TransactionType transactionType;

    //Transaction Method means it's a Banking transaction or RIDE transaction means the customer(Rider) is paying through Wallet.
    //Note-> In production Ready Code there will be a lot of enums.
    private TransactionMethod transactionMethod;

    @OneToOne //If this WalletTransaction is due to Ride so 1To1 Mapping with Ride and if it's not a Ride related Transaction then
    // this will be null.
    private Ride ride;

    @ManyToOne //One Wallet can have many walletTransactions . So Many walletTransactions are mapped to one Wallet.
    // Many=walletTransactions , one=Wallet
    private Wallet wallet;

    //If it's a banking related transaction then it would have a transactionID or UTR no. something like that.
    private String transactionId;

    @CreationTimestamp
    private LocalDateTime timeStamp;


}
