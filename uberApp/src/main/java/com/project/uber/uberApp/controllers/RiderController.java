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
@RequestMapping(path="/riders") //Global Path would be "/rider"
@RequiredArgsConstructor
@Secured("ROLE_RIDER")
//Note-> Before using @Secured, you must enable method-level security : @EnableMethodSecurity(securedEnabled = true) public class WebSecurityConfig {}
//inside WebSecurityConfig file else Spring will ignore all @Secured annotations.
public class RiderController {

    //Since we have declared RiderService as Required(i.e. As declared as final)
    private final RiderService riderService; //We can define a constructor manually or else we can write as @RequiredArgsConstructor

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

    //@RequestParam is used to extract query parameter values from the URL and bind them to method parameters in Spring Boot controller.
    //http://localhost:8080/search?name=Anmol&age=25 ,
    //@GetMapping("/search")
    //public String search(@RequestParam String name , @RequestParam Integer age) { return name + " " + age; }
    //PageRequest is made up of 2 things -> "PageNumber" → which page you want
    //  &  "PageSize" → how many records per page.
    @GetMapping(path = "/getAllMyRides")
    public ResponseEntity<Page<RideDto>> getAllMyRides(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                       @RequestParam(defaultValue = "10" , required = false)
                                                       Integer pageSize) //By default, required is "true" so making it false.
    {
        //Note -> Inside "PageRequest.of" we can also pass the "Sort.by" if we want our Data to be sorted acc. to some specific field.
        PageRequest pageRequest = PageRequest.of(pageNumber,pageSize,
                Sort.by(Sort.Direction.DESC,"createdTime","id")); //Sort by "createdTime" field in descending order.If 2 records
        //have the same "createdTime" , then sort by "id" field in descending order as well.
        //Note-> We are passing "createdTime" & "id" since both these fields are part of RideDto.
        return ResponseEntity.ok(riderService.getAllMyRides(pageRequest));
    }


    @PostMapping(path = "/logout")
    public ResponseEntity<UserDto> logout(){
        return ResponseEntity.ok(riderService.logout());
    }





















}
