package com.project.uber.uberApp.repositories;

import com.project.uber.uberApp.Entities.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction,Long> {
}
