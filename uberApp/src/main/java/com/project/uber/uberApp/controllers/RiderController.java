package com.project.uber.uberApp.controllers;

import com.project.uber.uberApp.Entities.RideRequest;
import com.project.uber.uberApp.dto.*;
import com.project.uber.uberApp.services.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/riders")
@RequiredArgsConstructor
@Secured("ROLE_RIDER")
public class RiderController {


    private final RiderService riderService;

    @PostMapping(path="/requestRide")
    public ResponseEntity<RideRequestDto> requestRide(@RequestBody RideRequestDto rideRequestDto){
    return ResponseEntity.ok(riderService.requestRide(rideRequestDto));
    }

    @PostMapping(path="/cancelRide/{rideId}")
    public ResponseEntity<RideDto> cancelRide(@PathVariable Long rideId ){
        return ResponseEntity.ok(riderService.cancelRide(rideId));
    }

    @PostMapping(path = "/rateDriver")
    public ResponseEntity<DriverDto> rateDriver(@RequestBody RatingDto ratingDto  ){
        return ResponseEntity.ok(riderService.rateDriver(ratingDto.getRideId() , ratingDto.getRating()));
    }

    @GetMapping(path = "/getMyProfile")
    public ResponseEntity<RiderDto> getMyProfile(){
        return ResponseEntity.ok(riderService.getMyProfile());
    }


    @GetMapping(path = "/getAllMyRides")
    public ResponseEntity<Page<RideDto>> getAllMyRides(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                       @RequestParam(defaultValue = "10" , required = false)
                                                       Integer pageSize) //By default, required is "true" so making it false.
    {

        PageRequest pageRequest = PageRequest.of(pageNumber,pageSize,
                Sort.by(Sort.Direction.DESC,"createdTime","id"));

        return ResponseEntity.ok(riderService.getAllMyRides(pageRequest));
    }


    @PostMapping(path = "/logout")
    public ResponseEntity<UserDto> logout(){
        return ResponseEntity.ok(riderService.logout());
    }





















}
