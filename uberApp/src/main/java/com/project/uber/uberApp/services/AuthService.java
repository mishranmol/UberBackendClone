package com.project.uber.uberApp.services;

import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.LoginResponseDto;
import com.project.uber.uberApp.dto.SignupDto;
import com.project.uber.uberApp.dto.UserDto;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    String[] login(String email, String Password);

    UserDto signup(SignupDto signupDto);

    DriverDto onboardNewDriver(Long userId , String vehicleId);

    String refreshAccessToken(String refreshToken);

    String logout(HttpServletRequest request, HttpServletResponse response);
}
