package com.project.uber.uberApp.repositories;

import com.project.uber.uberApp.Entities.Payment;
import com.project.uber.uberApp.Entities.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Optional<Payment> findByRide(Ride ride);
}
