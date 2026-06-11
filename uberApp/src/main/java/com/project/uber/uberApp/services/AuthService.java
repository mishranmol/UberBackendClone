package com.project.uber.uberApp.services;

import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.SignupDto;
import com.project.uber.uberApp.dto.UserDto;

//Deals with Authentication Related Concept(i.e->Login, SignUp, Logout)
public interface AuthService {

    //login will return token
    String login(String email, String Password);

    //signup will not require any SpringSecurity concept
    UserDto signup(SignupDto signupDto);

    //Take this userId and make him Driver
    DriverDto onboardNewDriver(Long userId);
}
