package com.project.uber.uberApp.services.implementations;

import com.project.uber.uberApp.services.DistanceService;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class DistanceServiceOSRMImpl implements DistanceService {

    //So here we'll call the Third Party API OSRM to find the distance.This API is used to calculate the distance b/t any 2 co-ordinates in Earth.
    //RestClient is used to call Third Party API's . Adding Spring WEB dependency will give the RestClient.
    //This API will calculate the Road Distance and not the co-ordinate distance because co-ordinate distance will give the Displacement.

    //In below API the co-ordinates are being passed in URl only in format of "longi,lati;" so will pass our co-ordinates in the Same way.
//private static final String OSRM_API_BASE_URL = "http://router.project-osrm.org/route/v1/driving/13.388860,52.517037;13.397634,52.529407?overview=false";

    private static final String OSRM_API_BASE_URL = "http://router.project-osrm.org/route/v1/driving/";

    @Override
    public double calculateDistance(Point src, Point dest) {

        //getX->Longitude , getY->Latitude
        String Uri = src.getX() + "," + src.getY() + ";" + dest.getX() + "," + dest.getY();

        //Putting this inside try-catch as it may give exception if the co-ordinates are wrong
        try {
            //RestClient is "Synchronous" -> Means our Code will stop here until we get the Data from the API
            OSRMResponseDto osrmResponseDto = RestClient.builder()///Creating an instance of RestClient by "RestClient.builder()"
                    .baseUrl(OSRM_API_BASE_URL)

                    .defaultHeader("Accept", "application/json") //Added extra,not in Anuj-bhaiya code ,
                    // This tells OSRM:"Please send the response in JSON format" . Since you're mapping the response
                    // into:OSRMResponseDto.class so you expect JSON . Without this header, most APIs still return "JSON" by
                    // default, but explicitly specifying it is a good practice.

                    .defaultHeader("Accept-Encoding", "identity") //added extra , not in Anuj-bhaiya code ,
                    //This tells OSRM:"Do NOT compress the response. Send the raw response."
                    .build()   // After doing the build we'll get the RestClient.
                    .get()   //GET Request, we have other methods as well like POST, PUT.
                    .uri(Uri)  //This URI will get appended in baseUrl.
                    .retrieve() //It's used to get all the Data from the API
                    .body(OSRMResponseDto.class);

            System.out.println(OSRM_API_BASE_URL + Uri);
            //We are getting the value at 0th index because routes are arranged in ASCENDING order.So index 0
            // will give us the shortest distance b/t those 2 co-ordinates.
            //We'll get distance in meters so converting it to KM by /1000 .
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

@Data //Adding this as to have all the Getter/setter methods
class OSRMResponseDto{
    //It will give us a List of routes , as between 2 co-ordinates multiple routes/paths can exist
    List<OSRMRoute> routes; //Make sure this "routes" syntax is same as the OSRM API_Response.
}

@Data
class OSRMRoute{
    private Double distance; //Make sure this "distance" syntax is same as the OSRM API_Response "distance"
    // so that Jackson can Map easily.
}


//Below is the OSRM_API_Response , from this we are getting array of "routes" and inside this "routes" we are only interested inside key="distance"
//{
//  "code": "Ok",
//  "routes": [
//    {
//      "legs": [
//        {
//          "steps": [],
//          "weight": 263.9,
//          "summary": "",
//          "duration": 260.4,
//          "distance": 1888
//        }
//      ],
//      "weight_name": "routability",
//      "weight": 263.9,
//      "duration": 260.4,
//      "distance": 1888
//    }
//  ],
//  "waypoints": [
//    {
//      "hint": "wgEKgDnGnoUXAAAABQAAAAAAAAAdAAAAuFtKQYXNK0AAAAAAIMuBQQsAAAADAAAAAAAAAA8AAADbIQEAFEzMAKpYIQM8TMwArVghAwAA3woAAAAA",
//      "location": [13.38882, 52.517034],
//      "name": "Friedrichstraße",
//      "distance": 2.73531597
//    },
//    {
//      "hint": "SiPfga-mkIcGAAAACQAAAAAAAABeAAAAz-ONQIv-tkAAAAAAeYZHQgYAAAAJAAAAAAAAAF4AAADbIQEAfm7MABmJIQOCbswA_4ghAwAATwUAAAAA",
//      "location": [13.39763, 52.529433],
//      "name": "Torstraße",
//      "distance": 2.905919634
//    }
//  ]
//}