package com.project.uber.uberApp.services.implementations;

import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.Entities.Rating;
import com.project.uber.uberApp.Entities.Ride;
import com.project.uber.uberApp.Entities.Rider;
import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.RiderDto;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.DriverRepository;
import com.project.uber.uberApp.repositories.RatingRepository;
import com.project.uber.uberApp.repositories.RiderRepository;
import com.project.uber.uberApp.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // This will create a constructor on our behalf.No need to create Manually
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;
    private final ModelMapper modelMapper;

    //We should create a Rating Object As soon as we start the Ride.
    @Override
    public DriverDto RateDriver(Ride ride , Integer rating) {

        Driver driver = ride.getDriver();

        //Getting the Rating for a particular Ride.
        Rating ratingObj = ratingRepository.findByRide(ride).
                orElseThrow( () -> new ResourceNotFoundException("Rating not found for ride with Id:"+ride.getId()));

        //Checking if the Driver has already got the Rating for this particular Ride , then Rider cannot rate the Driver again for this Ride.
        if(ratingObj.getDriverRating()!=null){
            throw new RuntimeException("Driver has already been rated, cannot rate again: "+ratingObj.getDriverRating());
        }

        //Giving Rating to Driver for this particular Ride.
        ratingObj.setDriverRating(rating);
        ratingRepository.save(ratingObj);

        //Calculating the Avg. rating of Driver for all his rides.
      Double newRating =  ratingRepository.findByDriver(driver)
                .stream()
                .mapToDouble(rating1 -> rating1.getDriverRating())//mapToDouble() is a Java Stream method that converts a stream
                //of objects into a stream of primitive double values (DoubleStream) so numerical operations like sum(), average(), min(),
                //and max() can be performed. InShort->mapToDouble() convert stream to DoubleStream so we can perform operations(sum,avg) easily.
               //Note -> We can also use Map(i.e->.map()) method here but then we can't use sum() directly.
                .average().orElse(0.0);

      driver.setRating(newRating);//Setting the "newRating = sum of all old Rating + This ride Rating"
      Driver savedDriver = driverRepository.save(driver);

      return modelMapper.map(savedDriver,DriverDto.class);

    }

    @Override
    //We have created a Rating Object As soon as we start the Ride.
    public RiderDto RateRider(Ride ride, Integer rating) {

        Rider rider = ride.getRider();

        //Getting the Rating for a particular Ride.
        Rating ratingObj = ratingRepository.findByRide(ride).
                orElseThrow( () -> new ResourceNotFoundException("Rating not found for ride with Id:"+ride.getId()));

        //Checking if the Rider has already got the Rating for this particular Ride , then Driver cannot rate the Rider again for this Ride.
        if(ratingObj.getRiderRating()!=null){
            throw new RuntimeException("Rider has already been rated, cannot rate again: "+ratingObj.getDriverRating());
        }

        //Giving Rating to Rider for this particular Ride.
        ratingObj.setRiderRating(rating);
        ratingRepository.save(ratingObj);

        //Calculating the Avg. rating of Rider for all his rides.
        Double newRating =  ratingRepository.findByRider(rider)
                .stream()
                .mapToDouble(rating1 -> rating1.getRiderRating())//mapToDouble() is a Java Stream method that converts a stream
                //of objects into a stream of primitive double values (DoubleStream) so numerical operations like sum(), average(), min(),
                //and max() can be performed. InShort->mapToDouble() convert stream to DoubleStream so we can perform operations(sum,avg) easily.
                //Note -> We can also use Map(i.e->.map()) method here but then we can't use sum() directly.
                .average().orElse(0.0);

        rider.setRating(newRating);//Setting the "newRating = sum of all old Rating + This ride Rating"
        Rider savedRider = riderRepository.save(rider);

        return  modelMapper.map(savedRider,RiderDto.class);



    }
}
