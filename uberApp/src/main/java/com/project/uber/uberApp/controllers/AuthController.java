package com.project.uber.uberApp.controllers;

import com.project.uber.uberApp.dto.*;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/auth")
public class AuthController {

    private final AuthService authService;


    @PostMapping(path = "/signup")
    ResponseEntity<UserDto> signUp(@RequestBody SignupDto signupDto) {
        return new ResponseEntity<>(authService.signup(signupDto), HttpStatus.CREATED);
    }



    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse){

        String[] tokens = authService.login(loginRequestDto.getEmail(),loginRequestDto.getPassword());

        Cookie cookie = new Cookie("refreshToken",tokens[1]);
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));

    }

    @PostMapping(path = "/refreshAccessToken")
    public ResponseEntity<LoginResponseDto> refreshAccessToken(HttpServletRequest request) throws ResourceNotFoundException {
            String refreshToken = Arrays.stream(request.getCookies()) //request.getCookies -> Returns all cookies sent by the browser.
                    .filter(cookie -> "refreshToken".equals(cookie.getName())) //Keeps only the cookie whose name is:refreshToken
                    .findFirst()//Gets the first matching cookie.
                    .map(cookie -> cookie.getValue()) //Gets the value store inside that cookies
                    .orElseThrow(() -> new ResourceNotFoundException("Token not found inside cookie"));

            String accessToken = authService.refreshAccessToken(refreshToken);

            return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }


    @Secured("ROLE_ADMIN")
    @PostMapping(path = "/onBoardNewDriver/{userId}")
    ResponseEntity<DriverDto> onboardNewDriver(@PathVariable Long userId , @RequestBody OnBoardDriverDto onBoardDriverDto) {
        return new ResponseEntity<>(authService.onboardNewDriver(userId,
                onBoardDriverDto.getVehicleId()),
                HttpStatus.CREATED);
    }

}
