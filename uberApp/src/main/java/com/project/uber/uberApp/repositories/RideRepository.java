package com.project.uber.uberApp.repositories;

import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.Entities.Ride;
import com.project.uber.uberApp.Entities.Rider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<Ride,Long> {

    //These methods will be created automatically by SpringData JPA and corresponding Queries are created by Hibernate.

    //Passed as "Pageable pageRequest" , as pageRequest is Implementation of Pageable.
    Page<Ride> findByRider(Rider rider, PageRequest pageRequest);

    //Passed as "Pageable pageRequest", as pageRequest is Implementation of Pageable.
    Page<Ride> findByDriver(Driver driver, Pageable pageRequest);

}
