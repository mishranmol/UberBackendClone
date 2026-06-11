package com.project.uber.uberApp.services.implementations;

import com.project.uber.uberApp.Entities.Rider;
import com.project.uber.uberApp.Entities.User;
import com.project.uber.uberApp.Entities.Wallet;
import com.project.uber.uberApp.Entities.enums.Role;
import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.SignupDto;
import com.project.uber.uberApp.dto.UserDto;
import com.project.uber.uberApp.exceptions.RuntimeConflictException;
import com.project.uber.uberApp.repositories.UserRepository;
import com.project.uber.uberApp.services.AuthService;
import com.project.uber.uberApp.services.RiderService;
import com.project.uber.uberApp.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@RequiredArgsConstructor
@Service
//We are doing this way means creating the implementation Separately for the Services because to Achieve Loose Coupling and
// improve the Readability of the Code
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RiderService riderService;
    private final WalletService walletService;

    @Override
    public String login(String email, String Password) {
        return "";
    }

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
        User savedUser = userRepository.save(mappedUser);

        //Every user is a Rider so we have to create Rider related entry as well.
        Rider rider = riderService.createNewRider(savedUser);

        //Every user will have a Wallet.
        //TODO -> Add wallet related service
        Wallet wallet = walletService.createNewWallet(savedUser);

        return modelMapper.map(savedUser,UserDto.class);

    }

    @Override
    public DriverDto onboardNewDriver(Long userId) {
        return null;
    }
}
