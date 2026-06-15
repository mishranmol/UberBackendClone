package com.project.uber.uberApp.services;

import com.project.uber.uberApp.Entities.Rider;
import com.project.uber.uberApp.Entities.User;
import com.project.uber.uberApp.dto.*;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

//It deals with all things related to Rider
public interface RiderService {


    RideRequestDto requestRide(RideRequestDto rideRequestDto) throws ResourceNotFoundException;

    //A "Rider" is allowed to Cancel a ride only if the RideStatus="CONFIRMED" , we are avoiding "ONGOING" state because then we have to
    //calculate the distance travelled from pickupLocation to the location where ride has cancelled and then calculate fare accordingly.
    RideDto cancelRide(Long rideId) throws ResourceNotFoundException;

    //For rating a Driver we need rating as well
    DriverDto rateDriver(Long rideId, Integer rating) throws ResourceNotFoundException;

    //driverId which will get from SpringSecurity ContextHolder
    RiderDto getMyProfile() throws ResourceNotFoundException;

    // This returns the data in Page Format , PageRequest contains the PageNo. and no. of Data user wants.
    // A Rider can have thousands of ride , so we won't send all rides in one go , hence using "PageRequest".
    Page<RideDto> getAllMyRides(PageRequest pageRequest) throws ResourceNotFoundException;

    //internal method so we can return Rider like this
    Rider createNewRider(User user);

    Rider getCurrentRider() throws ResourceNotFoundException;

    UserDto logout();
}
