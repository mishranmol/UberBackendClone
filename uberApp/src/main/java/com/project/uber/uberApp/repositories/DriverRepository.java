package com.project.uber.uberApp.repositories;

import com.project.uber.uberApp.Entities.Driver;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;



//Repository is used to fetch the Data from the entity(i.e. table)
@Repository
public interface DriverRepository extends JpaRepository<Driver,Long> {

//Inside Geo-Spatial DB there are some different types of queries which can be used to query Geo-Spa Data , these are very optimized queries
//in terms of getting distance, or getting point that are within specific KM Range . If we want to query such things then Geo-Spa DB is imp.
// ST_Distance(point1,point2) -> This is a PostGIS function used to calculate the distance between two geographic points.These methods
// are similar to SQL methods like MAX,MIN etc.
// ST_DWithin(point1,10000) -> THis is a PostGIS function that checks whether two locations are within a specified distance of each other.ex->
// ST_DWithin(location1, location2, distance). This is an boolean method , if condition met's returns true else false.

    //Below is a Native Query ->A Native Query is a SQL query written directly in the database's SQL language, instead of using JPQL (Java Persistence Query Language).
    //Each driver has a current_location entry.
    //Use colon(:) in pickupLocation to directly pass the value in query.
    //Space after distance,d is needed otherwise the query will break . Spaces are added while concatenating query strings to ensure SQL keywords
    //and clauses are separated correctly. Otherwise, the generated query may become invalid due to merged words.
    //10000 is 10km.
    //Interview Ques->We are going to each row and checking the point is within 10km or not so how this query is optimized and faster?
    //Interview Ans -> It's optimized due to indexing which is Post-GIS feature, Geo-spa DB creates indexes for our pickupLocation so it won't
    //go to all the entries in our DB it will only goto those entries which satisfy the given criteria.
    //Since our DB already supports POST-GIS so it will automatically understand ST_DWithin methods so we don't have to configure anything
    @Query(value = "Select d.*, ST_distance(d.current_location, :pickupLocation) AS distance " +
            "FROM driver d " +
            "where d.available = true AND ST_DWithin(d.current_location, :pickupLocation, 10000) " +
            "ORDER BY distance " +
            "LIMIT 10", nativeQuery = true)
   List<Driver> findTenNearestDrivers(Point pickupLocation);

    //Ques->In driver entity the currentLocation is written like this but in @Query it's written like current_location,why?
    //In JPA entities we use Java field names like currentLocation. Hibernate converts them to database column names such as
    //current_location. JPQL uses entity field names, while native SQL queries use actual database column names.
    @Query(value = "Select d.* " +
            "from driver d " +
            "where d.available = true AND ST_DWithin(d.current_location, :pickupLocation, 15000) " +
            "ORDER by d.rating DESC " +
            "LIMIT 10", nativeQuery = true)
    List<Driver> findTenNearbyTopRatedDrivers(Point pickupLocation);

}
