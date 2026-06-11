package com.project.uber.uberApp.repositories;

import com.project.uber.uberApp.Entities.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiderRepository extends JpaRepository<Rider,Long> {
}
