package com.project.uber.uberApp.services.implementations;

import com.project.uber.uberApp.Entities.*;
import com.project.uber.uberApp.Entities.enums.RideRequestStatus;
import com.project.uber.uberApp.Entities.enums.RideStatus;
import com.project.uber.uberApp.dto.*;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.RideRepository;
import com.project.uber.uberApp.repositories.RideRequestRepository;
import com.project.uber.uberApp.repositories.RiderRepository;
import com.project.uber.uberApp.services.DriverService;
import com.project.uber.uberApp.services.RatingService;
import com.project.uber.uberApp.services.RideService;
import com.project.uber.uberApp.services.RiderService;
import com.project.uber.uberApp.strategies.DriverMatchingStrategy;
import com.project.uber.uberApp.strategies.RideFareCalculationStrategy;
import com.project.uber.uberApp.strategies.RideStrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiderServiceImpl implements RiderService {

    // Using "final" so that no one can change that thing anywhere inside the application but if we will use @Autowired so there will
    // be no "final" hence we use "Constructor Injection" rather than @Autowired.
    // Rather can writing the Constructor we will use @RequiredArgsConstructor which will create Constructor for us so no need to write manually.

    private final ModelMapper modelMapper;
    private  final RideStrategyManager rideStrategyManager;
    private final RideRequestRepository rideRequestRepository;
    private final RiderRepository riderRepository;
    private final RideService rideService;
    private final DriverService driverService;
    private final RatingService ratingService;
    private final JWTService jwtService;

    @Override
    @Transactional//To maintain "Atomicity" -> Either Everything will execute or Nothing will execute.By Adding @Trans.
    //the whole method comes under a Transaction Context so if anything fails or goes Wrong then everything RollsBack.
    public RideRequestDto requestRide(RideRequestDto rideRequestDto){

        RideRequest rideRequest = modelMapper.map(rideRequestDto,RideRequest.class);
//      log.info(rideRequest.toString()); after putting the breakpoint we can to know that modelmapper is not able to fill the
// pickupLocation/dropOffLocation because in Entity it's Point but in rideRequestDto it's PointDto so it was unable to convert
// these two so we have to make some configuration changes in our modelmapper like ->  modelMapper.typeMap...
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
        Rider rider = getCurrentRider();
        rideRequest.setRider(rider);

       Double fare = rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest);
       rideRequest.setFare(fare);

        //No matter we get the driver or not , we'll save our RideRequest
        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);

        //This will broadcast a msg to all the drivers who are matched so we can't do anything with this because now it's totally
        //Up to the drivers that whether they will accept/reject this particulate rideRequest.

      List<Driver> drivers =  rideStrategyManager.driverMatchingStrategy(rider.getRating()).
              findMatchingDriver(rideRequest);
        //Todo -> Send Notifications to all drivers regarding this rideRequest

        // Currently the rideRequest which is returned has status=PENDING, this will be moved to "CONFIRMED" once driver accepts
        // this ride request so this will come under driver's method of accepting the request.
       return modelMapper.map(savedRideRequest,RideRequestDto.class);

    }

    @Override
    public RideDto cancelRide(Long rideId) {

        Rider currentRider = getCurrentRider();
        Ride currentRide = rideService.getRideById(rideId);

        //Checking if this rider owns this Ride
        if(!currentRide.getRider().equals(currentRider)){
            throw new RuntimeException("Not allowed to cancel as Rider does not own this ride with id: "+rideId);
        }

        //Rider only allowed to cancel if status="CONFIRMED"
        if(!currentRide.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RuntimeException("Ride cannot be cancelled, invalid status: "+currentRide.getRideStatus());
        }

        Ride savedRide = rideService.updateRideStatus(currentRide,RideStatus.CANCELLED);
        Driver savedDriver = driverService.updateDriverAvailability(currentRide.getDriver(), true);

        return modelMapper.map(savedRide,RideDto.class);


    }

    @Override
    public DriverDto rateDriver(Long rideId, Integer rating) {

        Ride ride = rideService.getRideById(rideId);

        Rider currentRider = getCurrentRider();

        //Checking if the currentDriver owns this ride or not , if not then Driver cannot rate this Rider.
        if(!ride.getRider().equals(currentRider)){
            throw new RuntimeException("Cannot give the rating as Driver doesn't own's this ride"+currentRider);
        }

        //If the ride is not ENDED then Rider cannot ride the Driver.
        if(!ride.getRideStatus().equals(RideStatus.ENDED)){
            throw new RuntimeException("Driver cannot rate the Rider as Ride is not yet Ended"+ride.getRideStatus());
        }

        return ratingService.RateDriver(ride,rating);
    }

    @Override
    public RiderDto getMyProfile() {
        Rider rider = getCurrentRider();
        return modelMapper.map(rider,RiderDto.class);
    }

    @Override
    public Page<RideDto> getAllMyRides(PageRequest pageRequest) throws ResourceNotFoundException {
        Rider CurrentRider = getCurrentRider(); //This func gives the details of the currentRider loggedIn using SpringSecurityContext.
        return rideService.getAllRidesOfRider(CurrentRider,pageRequest).
                map(ride -> modelMapper.map(ride,RideDto.class));
    }

    @Override
    public Rider createNewRider(User user) {
        //Using builder pattern to create a Rider , so adding @Builder inside Rider entity.
        //We would have also used Rider rider = new Rider();
        Rider rider = Rider.
                builder().
                user(user).
                rating(0.0).
                build();
        riderRepository.save(rider); //After creating a New Rider , saving its details inside DB.
        return rider;
    }

    //This method will return the current Rider Data so when we implement SpringSecurity then we are going to have the Context of the current User then using that User we can get the Rider
    @Override
    public Rider getCurrentRider() {

        //TODO -> Implement SpringSecurity , DONE

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return riderRepository.findByUser(user).orElseThrow(() ->
                new ResourceNotFoundException("Rider not associated with user with given Id: "+1));
    }

    @Override
    public UserDto logout() {
        return null;
    }
}
