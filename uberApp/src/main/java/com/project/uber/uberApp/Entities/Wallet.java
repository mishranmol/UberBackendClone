package com.project.uber.uberApp.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

//Wallet will indicate the balance and WalletTransactions as well
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //"optional = false" means:The associated entity must exist.It cannot be null.More means that we have to pass the "User"
    //if we want to create a wallet , the User will cannot be null.
    @OneToOne(fetch = FetchType.LAZY , optional = false)//A Rider is User , a driver is also a User so everyone can have a wallet so that's why
    // OneToOne Mapping with Wallet is done.
    private User user;

    private Double balance = 0.0;

    // This Wallet can contain a lot of transaction , like adding funds from bank into the wallet OR Debit Transaction in which Driver is
    // actually Cashing out the payment from his wallet to his Bank Account , so we have to create WalletTransaction Entity.
    @OneToMany(mappedBy = "wallet" ,  fetch = FetchType.LAZY)
    private List<WalletTransaction> transactions;

}
