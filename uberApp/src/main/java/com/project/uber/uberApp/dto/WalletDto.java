package com.project.uber.uberApp.dto;

import com.project.uber.uberApp.Entities.User;
import com.project.uber.uberApp.Entities.WalletTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletDto {

    private Long id;

    private UserDto user;

    private Double balance;

    private List<WalletTransactionDto> transactions;
}
