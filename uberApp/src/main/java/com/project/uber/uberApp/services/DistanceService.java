package com.project.uber.uberApp.services;


import org.locationtech.jts.geom.Point;
import org.springframework.web.client.RestClient;

public interface DistanceService {

    // We can use OSRM or Google Map implementation for calculating the distance.
    // OSRM is unPaid and GoogleMap is Paid.
    // SO here we will call the third Party API OSRM to find the distance.Will be using RestClient to do that.
    // Adding Spring WEB dependency will give the RestClient.
    double calculateDistance(Point src, Point dest);

}