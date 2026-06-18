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


    private Double amount;


    private TransactionType transactionType;


    private TransactionMethod transactionMethod;


    @ManyToOne
    private Ride ride;

    @ManyToOne
    private Wallet wallet;

    private String transactionId;

    @CreationTimestamp
    private LocalDateTime timeStamp;


}
