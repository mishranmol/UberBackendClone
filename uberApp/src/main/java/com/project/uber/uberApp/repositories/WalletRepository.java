package com.project.uber.uberApp.repositories;

import com.project.uber.uberApp.Entities.User;
import com.project.uber.uberApp.Entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {

    //Optional are used to deal with the null's.
    //Optional<T> is a Java container that represents a value that may or may not be present,helping avoid NullPointerException.
    Optional<Wallet> findByUser(User user);
}
