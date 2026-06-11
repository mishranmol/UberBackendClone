package com.project.uber.uberApp.controllers;

import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.RideDto;
import com.project.uber.uberApp.dto.RideStartDto;
import com.project.uber.uberApp.services.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "drivers")
public class DriversController {

    private final DriverService driverService;

    @PostMapping(path = "/acceptRide/{rideRequestId}")
    public ResponseEntity<RideDto> acceptRide(@PathVariable Long rideRequestId){
        return ResponseEntity.ok(driverService.acceptRide(rideRequestId));
    }

    //To start the ride Driver needs the OTP
    @PostMapping(path = "/startRide/{rideRequestId}")
    public ResponseEntity<RideDto> startRide(@PathVariable Long rideRequestId , @RequestBody RideStartDto rideStartDto){
        return ResponseEntity.ok(driverService.startRide(rideRequestId,rideStartDto.getOtp()));
    }

    @PostMapping(path = "/endRide/{rideId}")
    public ResponseEntity<RideDto> endRide(@PathVariable Long rideId){
        return ResponseEntity.ok(driverService.endRide(rideId));
    }

    @PostMapping(path = "/cancelRide/{rideRequestId}")
    public ResponseEntity<RideDto> cancelRide(@PathVariable Long rideRequestId){
        return ResponseEntity.ok(driverService.cancelRide(rideRequestId));
    }



    @GetMapping(path = "/getMyProfile")
    public ResponseEntity<DriverDto> getMyProfile(){
        return ResponseEntity.ok(driverService.getMyProfile());
    }

    @GetMapping(path = "/getAllMyRides")
    public ResponseEntity<Page<RideDto>> getAllMyRides(PageRequest pageRequest){
        return ResponseEntity.ok(driverService.getAllMyRides(pageRequest));
    }


}
