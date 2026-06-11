package com.project.uber.uberApp.dto;

import com.project.uber.uberApp.Entities.Ride;
import com.project.uber.uberApp.Entities.Wallet;
import com.project.uber.uberApp.Entities.enums.TransactionMethod;
import com.project.uber.uberApp.Entities.enums.TransactionType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletTransactionDto {

    private Long id;

    private Double amount;

    //Each Transaction has some type --> CREDIT,DEBIT
    private TransactionType transactionType;

    //Transaction Method means it's a Banking transaction or RIDE transaction means the customer(Rider) is paying through Wallet.
    //Note-> In production Ready Code there will be a lot of enums.
    private TransactionMethod transactionMethod;

    private RideDto ride;

    //If it's a banking related transaction then it would have a transactionID or UTR no. something like thah.
    private String transactionId;

    private WalletDto wallet;

    private LocalDateTime timeStamp;
}
