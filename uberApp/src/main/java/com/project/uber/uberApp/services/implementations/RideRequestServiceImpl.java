package com.project.uber.uberApp.services.implementations;

import com.project.uber.uberApp.Entities.RideRequest;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.RideRequestRepository;
import com.project.uber.uberApp.services.RideRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideRequestServiceImpl implements RideRequestService {

    private final RideRequestRepository rideRequestRepository;

    @Override
    public RideRequest findRideRequestById(Long rideRequestId) {
        try {
            return rideRequestRepository.findById(rideRequestId).
                    orElseThrow(() -> new ResourceNotFoundException("RideRequest with given Id not found : "+ rideRequestId));
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(RideRequest rideRequest) {
        rideRequestRepository.findById(rideRequest.getId()).
                    orElseThrow(() -> new ResourceNotFoundException("RideRequest not found with id : "+rideRequest.getId()));
        rideRequestRepository.save(rideRequest);
    }
}
