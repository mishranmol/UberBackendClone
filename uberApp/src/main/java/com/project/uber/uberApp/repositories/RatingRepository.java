package com.project.uber.uberApp.repositories;

import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.Entities.Rating;
import com.project.uber.uberApp.Entities.Ride;
import com.project.uber.uberApp.Entities.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
//Note -> Jo Bhi hamari Entity ka naam hoga wahi cheez hum logo ko return mai milegi , jaise yeah Entity ka naam Rating hai toh query
//karna par response mai Rating hi milega kuki Entity is a table inside DB so whatever DB operation we'll execute would take place inside that table/Entity only.
public interface RatingRepository extends JpaRepository<Rating,Long> {

    List<Rating> findByRider(Rider rider);
    List<Rating> findByDriver(Driver driver);

    Optional<Rating> findByRide(Ride ride);
}
