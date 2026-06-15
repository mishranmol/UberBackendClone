package com.project.uber.uberApp.services;

import com.project.uber.uberApp.Entities.User;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

//NOTE ->  private final UserRepository userRepository;
//If we are Using @RequiredArgsConstructor to inject the constructor but if we'll remove "final" Keyword from above line then it will give error
//because @RequiredArgsConstructor only generates a constructor for fields that are marked as "final" or marked with "@NonNull".

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)  {
        return userRepository.findByEmail(username).
                orElseThrow(() -> new UsernameNotFoundException("User not found with given Email: "+username));
    }

    public User getUserFromId(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Id: "+userId));
    }
}
