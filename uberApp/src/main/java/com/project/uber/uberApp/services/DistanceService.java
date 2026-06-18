package com.project.uber.uberApp.services;


import org.locationtech.jts.geom.Point;
import org.springframework.web.client.RestClient;

public interface DistanceService {
    double calculateDistance(Point src, Point dest);
}