package com.project.uber.uberApp.repositories;

import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.Entities.User;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


//Repository is used to fetch the Data from the entity(i.e. table)
@Repository
public interface DriverRepository extends JpaRepository<Driver,Long> {

    @Query(value = "Select d.*, ST_distance(d.current_location, :pickupLocation) AS distance " +
            "FROM driver d " +
            "where d.available = true AND ST_DWithin(d.current_location, :pickupLocation, 10000) " +
            "ORDER BY distance " +
            "LIMIT 10", nativeQuery = true)
   List<Driver> findTenNearestDrivers(Point pickupLocation);


    @Query(value = "Select d.* " +
            "from driver d " +
            "where d.available = true AND ST_DWithin(d.current_location, :pickupLocation, 15000) " +
            "ORDER by d.rating DESC " +
            "LIMIT 10", nativeQuery = true)
    List<Driver> findTenNearbyTopRatedDrivers(Point pickupLocation);

    Optional<Driver> findByUser(User user);
}
