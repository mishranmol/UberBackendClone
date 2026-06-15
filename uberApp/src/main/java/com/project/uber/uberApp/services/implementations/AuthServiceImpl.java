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
//We are doing this way means creating the implementation Separately for the Services because to Achieve Loose Coupling and
// improve the Readability of the Code
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final RiderService riderService;
    private final WalletService walletService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserService userService;

    ////Using Authentication Manager to authenticate user.
    @Override
    public String[] login(String email, String Password) {
        String[] tokens = new String[2];

        //The authenticationManager will take "authentication" as input & it will give "authentication" as Output as well.
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
    @Transactional //By adding this annotation this whole method goes under Transaction Context so either everything will get executed or it will Rollback.
    //Note-> If we are writing in multiple tables in a function make sure to use this annotation so that your Data is consistent throughout.
    //There's issue in below code , issue that suppose if we are successfully able to save our User inside user table but
    //unable to createRider by some reason and due to that we're not able to save the rider in Rider table so now we have Data inconsistency
    //inside our DB , so will add @Transactional which means if certain operations are performed then either all of them will occur
    // or if any operation fail then will Rollback.
    //This method is used when user creates account for the 1st time -> Onboarding Process
    public UserDto signup(SignupDto signupDto) {

        //Using mail because over mail we have added a unique Constraint
        User userExists = userRepository.findByEmail(signupDto.getEmail()).orElse(null);

        if(userExists != null){
            throw new RuntimeException("Cannot SignUp, User already exists with email"+ signupDto.getEmail());
        }

       User mappedUser = modelMapper.map(signupDto, User.class);
       mappedUser.setRoles(Set.of(Role.RIDER));//By default, if any user log's in into our system so will assign Role="RIDER".

        //passwordEncoder.encode() -> It's used to convert a plain-text password into a secure hashed password before saving it in DB.
        mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));

        User savedUser = userRepository.save(mappedUser);

        //Every user is a Rider so we have to create Rider related entry as well.
        Rider rider = riderService.createNewRider(savedUser);

        //Every user will have a Wallet.
        //TODO -> Add wallet related service
        Wallet wallet = walletService.createNewWallet(savedUser);

        return modelMapper.map(savedUser,UserDto.class);

    }

    //Done By own
    @Override
    public DriverDto onboardNewDriver(Long userId , String vehicleId) {

        User user = userRepository.findById(userId).
                orElseThrow(() -> new ResourceNotFoundException("User not found with given id: "+userId));

        //Below we are importing Statically means instead of Role.DRIVER only DRIVER.
        if(user.getRoles().contains(DRIVER)){
            throw new RuntimeException("The current User is already a Driver");
        }

        //Since the user with given userId is not a "Driver" so create a DriverEntity.
        Driver newDriver = Driver.builder()
                .user(user)
                .rating(0.0)
                .available(true)
                .vehicleId(vehicleId)
                .build();

        //After creating Driver Entity Setting the Role=DRIVER inside User Entity as well.
        user.getRoles().add(DRIVER);
        userRepository.save(user); //Updating the UserRole inside DB.

        Driver savedDriver = driverRepository.save(newDriver); //Updating the Driver inside DB.

        return modelMapper.map(savedDriver,DriverDto.class);

    }

    @Override
    public String refreshAccessToken(String refreshToken){
        Long userId = jwtService.getUserIdFromToken(refreshToken); //As we have stores ID inside the Subject while creating the refreshToken.
        User user = userRepository.findById(userId).
                orElseThrow(() -> new ResourceNotFoundException("user not found with Id: "+userId));
        return jwtService.generateAccessToken(user);

    }
}
