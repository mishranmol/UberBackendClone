package com.project.uber.uberApp.services.implementations;

import com.project.uber.uberApp.services.DistanceService;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class DistanceServiceOSRMImpl implements DistanceService {

    private static final String OSRM_API_BASE_URL = "http://router.project-osrm.org/route/v1/driving/";

    @Override
    public double calculateDistance(Point src, Point dest) {

        //getX->Longitude , getY->Latitude
        String Uri = src.getX() + "," + src.getY() + ";" + dest.getX() + "," + dest.getY();


        try {

            OSRMResponseDto osrmResponseDto = RestClient.builder()
                    .baseUrl(OSRM_API_BASE_URL)

                    .defaultHeader("Accept", "application/json")

                    .defaultHeader("Accept-Encoding", "identity")
                    .build()
                    .get()
                    .uri(Uri)
                    .retrieve()
                    .body(OSRMResponseDto.class);

            System.out.println(OSRM_API_BASE_URL + Uri);

            return osrmResponseDto.getRoutes().getFirst().getDistance() / 1000.0;

        }catch (Exception e){
            System.out.println(OSRM_API_BASE_URL + Uri);
            e.printStackTrace();
            throw new RuntimeException(
                    "Error in getting the Data from OSRM", e
            );
        }
    }
}

@Data
class OSRMResponseDto{
    List<OSRMRoute> routes;
}

@Data
class OSRMRoute{
    private Double distance;
}
