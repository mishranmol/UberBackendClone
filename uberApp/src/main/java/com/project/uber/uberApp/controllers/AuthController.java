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

    //SignUp is used to create a new User so use HttpStatus.CREATED.
    @PostMapping(path = "/signup")
    ResponseEntity<UserDto> signUp(@RequestBody SignupDto signupDto) {
        return new ResponseEntity<>(authService.signup(signupDto), HttpStatus.CREATED);
    }


    //As login will return the token(JWT) , hence return type=String.
    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse){

        String[] tokens = authService.login(loginRequestDto.getEmail(),loginRequestDto.getPassword());

        //We can set a lot of information about this cookie .
        //We are doing setHttpOnly(true); so that this cookie cannot be accessed by any another means , and it can only be found with help of
        //HTTP methods and only the loggedIn user can have access to this cookie .
        //httpOnly cookies can be passed from the backend to the frontend .
        Cookie cookie = new Cookie("refreshToken",tokens[1]);
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));

    }

/*
Why HttpServletRequest?
HttpServletRequest represents the incoming HTTP request sent by the client.
It gives access to:
Headers
Cookies
Query Parameters
Request Body
Request URL ,
In your case, you need it because the refresh token is stored inside a cookie.
*/

    @PostMapping(path = "/refreshAccessToken")
    public ResponseEntity<LoginResponseDto> refreshAccessToken(HttpServletRequest request) throws ResourceNotFoundException {
            String refreshToken = Arrays.stream(request.getCookies()) //request.getCookies -> Returns all cookies sent by the browser.
                    .filter(cookie -> "refreshToken".equals(cookie.getName())) //Keeps only the cookie whose name is:refreshToken
                    .findFirst()//Gets the first matching cookie.
                    .map(cookie -> cookie.getValue()) //Gets the value store inside that cookies
                    .orElseThrow(() -> new ResourceNotFoundException("Token not found inside cookie"));

            String accessToken = authService.refreshAccessToken(refreshToken);

            return ResponseEntity.ok(new LoginResponseDto(accessToken));
/*
Client sends request
        │
        ▼
HttpServletRequest received
        │
        ▼
Read all cookies
        │
        ▼
Find cookie named "refreshToken"
        │
        ├── Found
        │      ▼
        │   Get its value
        │      ▼
        │  refreshToken = "abc123"
        │
        └── Not Found
               ▼
        throw ResourceNotFoundException
               ▼
        GlobalExceptionHandler catches it
               ▼
        Error response returned
 */
    }


    @Secured("ROLE_ADMIN") //This API can be called only by Admin's.
    // Note-> Before using @Secured, you must enable method-level security : @EnableMethodSecurity(securedEnabled = true) public class WebSecurityConfig {}
    //inside WebSecurityConfig file else Spring will ignore all @Secured annotations.
    @PostMapping(path = "/onBoardNewDriver/{userId}")
    ResponseEntity<DriverDto> onboardNewDriver(@PathVariable Long userId , @RequestBody OnBoardDriverDto onBoardDriverDto) {
        return new ResponseEntity<>(authService.onboardNewDriver(userId,
                onBoardDriverDto.getVehicleId()),
                HttpStatus.CREATED);
    }


}
