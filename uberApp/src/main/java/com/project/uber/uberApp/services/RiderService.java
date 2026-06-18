package com.project.uber.uberApp.services;

import com.project.uber.uberApp.Entities.Rider;
import com.project.uber.uberApp.Entities.User;
import com.project.uber.uberApp.dto.*;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;


public interface RiderService {


    RideRequestDto requestRide(RideRequestDto rideRequestDto) throws ResourceNotFoundException;

    RideDto cancelRide(Long rideId) throws ResourceNotFoundException;


    DriverDto rateDriver(Long rideId, Integer rating) throws ResourceNotFoundException;

    RiderDto getMyProfile() throws ResourceNotFoundException;

    Page<RideDto> getAllMyRides(PageRequest pageRequest) throws ResourceNotFoundException;

    Rider createNewRider(User user);

    Rider getCurrentRider() throws ResourceNotFoundException;

    UserDto logout();
}
