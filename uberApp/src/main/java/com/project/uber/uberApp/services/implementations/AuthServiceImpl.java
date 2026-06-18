package com.project.uber.uberApp.services.implementations;

import com.project.uber.uberApp.Entities.Driver;
import com.project.uber.uberApp.Entities.Rider;
import com.project.uber.uberApp.Entities.User;
import com.project.uber.uberApp.Entities.Wallet;
import com.project.uber.uberApp.Entities.enums.Role;
import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.LoginResponseDto;
import com.project.uber.uberApp.dto.SignupDto;
import com.project.uber.uberApp.dto.UserDto;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.exceptions.RuntimeConflictException;
import com.project.uber.uberApp.repositories.DriverRepository;
import com.project.uber.uberApp.repositories.UserRepository;
import com.project.uber.uberApp.services.AuthService;
import com.project.uber.uberApp.services.RiderService;
import com.project.uber.uberApp.services.UserService;
import com.project.uber.uberApp.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.project.uber.uberApp.Entities.enums.Role.DRIVER;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final RiderService riderService;
    private final WalletService walletService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserService userService;


    @Override
    public String[] login(String email, String Password) {
        String[] tokens = new String[2];

       Authentication authentication =
               authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,Password));

       User user = (User) authentication.getPrincipal();

       String accessToken = jwtService.generateAccessToken(user);
       String refreshToken = jwtService.generateRefreshToken(user);

       tokens[0]=accessToken;
       tokens[1]=refreshToken;

        return tokens;
    }

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto signup(SignupDto signupDto) {

        //Using mail because over mail we have added a unique Constraint
        User userExists = userRepository.findByEmail(signupDto.getEmail()).orElse(null);

        if(userExists != null){
            throw new RuntimeException("Cannot SignUp, User already exists with email"+ signupDto.getEmail());
        }

       User mappedUser = modelMapper.map(signupDto, User.class);
       mappedUser.setRoles(Set.of(Role.RIDER));

        //passwordEncoder.encode() -> It's used to convert a plain-text password into a secure hashed password
        //before saving it in DB.
        mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));

        User savedUser = userRepository.save(mappedUser);

        Rider rider = riderService.createNewRider(savedUser);

        //TODO -> Add wallet related service
        Wallet wallet = walletService.createNewWallet(savedUser);

        return modelMapper.map(savedUser,UserDto.class);

    }


    @Override
    public DriverDto onboardNewDriver(Long userId , String vehicleId) {

        User user = userRepository.findById(userId).
                orElseThrow(() -> new ResourceNotFoundException("User not found with given id: "+userId));

        if(user.getRoles().contains(DRIVER)){
            throw new RuntimeException("The current User is already a Driver");
        }


        Driver newDriver = Driver.builder()
                .user(user)
                .rating(0.0)
                .available(true)
                .vehicleId(vehicleId)
                .build();

        user.getRoles().add(DRIVER);
        userRepository.save(user);

        Driver savedDriver = driverRepository.save(newDriver);

        return modelMapper.map(savedDriver,DriverDto.class);

    }

    @Override
    public String refreshAccessToken(String refreshToken){
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId).
                orElseThrow(() -> new ResourceNotFoundException("user not found with Id: "+userId));
        return jwtService.generateAccessToken(user);

    }
}
