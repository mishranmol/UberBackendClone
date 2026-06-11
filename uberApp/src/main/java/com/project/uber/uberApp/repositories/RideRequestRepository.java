package com.project.uber.uberApp.repositories;

import com.project.uber.uberApp.Entities.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest,Long> {
}
