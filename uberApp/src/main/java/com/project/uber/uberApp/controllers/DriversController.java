package com.project.uber.uberApp.controllers;

import com.project.uber.uberApp.dto.*;
import com.project.uber.uberApp.services.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "drivers")
@Secured("ROLE_DRIVER")//It means only user with "Role=DRIVER" would be allowed to hit the API's under this DriverController
// else no one(i.e. RIDER) would NOT be allowed to hit the API's of this DriverController.
//Note-> Before using @Secured, you must enable method-level security : @EnableMethodSecurity(securedEnabled = true) public class WebSecurityConfig {}
//inside WebSecurityConfig file else Spring will ignore all @Secured annotations.
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

    @PostMapping(path = "/cancelRide/{rideId}")
    public ResponseEntity<RideDto> cancelRide(@PathVariable Long rideId){
        return ResponseEntity.ok(driverService.cancelRide(rideId));
    }

    @PostMapping(path = "/rateRider")
    public ResponseEntity<RiderDto> rateRider(@RequestBody RatingDto ratingDto){
        return ResponseEntity.ok(driverService.rateRider(ratingDto.getRideId() , ratingDto.getRating()));
    }

    @GetMapping(path = "/getMyProfile")
    public ResponseEntity<DriverDto> getMyProfile(){
        return ResponseEntity.ok(driverService.getMyProfile());
    }

    //@RequestParam is used to extract query parameter values from the URL and bind them to method parameters in Spring Boot controller.
    //http://localhost:8080/search?name=Anmol&age=25 ,
    //@GetMapping("/search")
    //public String search(@RequestParam String name , @RequestParam Integer age) { return name + " " + age; }
    //PageRequest is made up of 2 things -> "PageNumber" → which page you want
                                      //  &  "PageSize" → how many records per page.
    @GetMapping(path = "/getAllMyRides")
    public ResponseEntity<Page<RideDto>> getAllMyRides(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                       @RequestParam(defaultValue = "10" , required = false) Integer pageSize) //By default, required is "true" so making it false.
    {
        //Note -> Inside "PageRequest.of" we can also pass the "Sort.by" if we want our Data to be sorted acc. to some specific field.
        PageRequest pageRequest = PageRequest.of(pageNumber,pageSize,
                Sort.by(Sort.Direction.DESC,"createdTime","id")); //Sort by "createdTime" field in descending order.If 2 records
        //have the same "createdTime" , then sort by "id" field in descending order as well.
        //Note-> We are passing "createdTime" & "id" since both these fields are part of RideDto.
        return ResponseEntity.ok(driverService.getAllMyRides(pageRequest));
    }


}
