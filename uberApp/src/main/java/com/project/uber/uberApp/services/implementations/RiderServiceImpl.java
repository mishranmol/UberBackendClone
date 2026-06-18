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


    private final ModelMapper modelMapper;
    private  final RideStrategyManager rideStrategyManager;
    private final RideRequestRepository rideRequestRepository;
    private final RiderRepository riderRepository;
    private final RideService rideService;
    private final DriverService driverService;
    private final RatingService ratingService;
    private final JWTService jwtService;

    @Override
    @Transactional
    public RideRequestDto requestRide(RideRequestDto rideRequestDto){

        RideRequest rideRequest = modelMapper.map(rideRequestDto,RideRequest.class);

        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
        Rider rider = getCurrentRider();
        rideRequest.setRider(rider);

       Double fare = rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest);
       rideRequest.setFare(fare);

        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);


      List<Driver> drivers =  rideStrategyManager.driverMatchingStrategy(rider.getRating()).
              findMatchingDriver(rideRequest);
        //Todo -> Send Notifications to all drivers regarding this rideRequest

       return modelMapper.map(savedRideRequest,RideRequestDto.class);

    }

    @Override
    public RideDto cancelRide(Long rideId) {

        Rider currentRider = getCurrentRider();
        Ride currentRide = rideService.getRideById(rideId);


        if(!currentRide.getRider().equals(currentRider)){
            throw new RuntimeException("Not allowed to cancel as Rider does not own this ride with id: "+rideId);
        }


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


        if(!ride.getRider().equals(currentRider)){
            throw new RuntimeException("Cannot give the rating as Driver doesn't own's this ride"+currentRider);
        }


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
        Rider CurrentRider = getCurrentRider();
        return rideService.getAllRidesOfRider(CurrentRider,pageRequest).
                map(ride -> modelMapper.map(ride,RideDto.class));
    }

    @Override
    public Rider createNewRider(User user) {
        Rider rider = Rider.
                builder().
                user(user).
                rating(0.0).
                build();
        riderRepository.save(rider);
        return rider;
    }


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
