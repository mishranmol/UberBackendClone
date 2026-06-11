package com.project.uber.uberApp.controllers;

import com.project.uber.uberApp.Entities.RideRequest;
import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.RideDto;
import com.project.uber.uberApp.dto.RideRequestDto;
import com.project.uber.uberApp.dto.RiderDto;
import com.project.uber.uberApp.services.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/riders") //Global Path would be "/rider"
@RequiredArgsConstructor
public class RiderController {

    //Since we have declared RiderService as Required(i.e. As declared as final)
    private final RiderService riderService; //We can define a constructor manually or else we can write as @RequiredArgsConstructor

    @PostMapping(path="/requestRide")
    public ResponseEntity<RideRequestDto> requestRide(@RequestBody RideRequestDto rideRequestDto){
    return ResponseEntity.ok(riderService.requestRide(rideRequestDto));
    }

    @PostMapping(path="/cancelRide/{rideRequestId}")
    public ResponseEntity<RideDto> cancelRide(@PathVariable RideRequest rideRequestId){
        return ResponseEntity.ok(riderService.cancelRide(rideRequestId.getId()));
    }

    @GetMapping(path = "/getMyProfile")
    public ResponseEntity<RiderDto> getMyProfile(){
        return ResponseEntity.ok(riderService.getMyProfile());
    }

    @GetMapping(path = "/getAllMyRides")
    public ResponseEntity<Page<RideDto>> getAllMyRides(PageRequest pageRequest){
        return ResponseEntity.ok(riderService.getAllMyRides(pageRequest));
    }






















}
