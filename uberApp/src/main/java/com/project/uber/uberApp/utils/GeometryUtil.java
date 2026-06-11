package com.project.uber.uberApp.utils;

import com.project.uber.uberApp.dto.PointDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryUtil {

    public static Point createPoint(PointDto pointDto){
        //To convert PointDto to Point will make use of GeometryFactory.
        //GeometryFactory is a factory class used to create Point . It has a method called createPoint using which it creates Point.
        //GeometryFactory will take 2 things -> new PrecisionModel , 2nd-> SRID(Spatial Reference Identifier), so SRID for Earth is 4326.
        //So this geometryFactory will generate Points related to the Earth's Coordinates & then we can use it.
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),4326);
        //pointDto contains the coordinates array , 0->Longitude,[1]-> Latitude
        Coordinate coordinate = new Coordinate(pointDto.getCoordinates()[0],pointDto.getCoordinates()[1]);
        return geometryFactory.createPoint(coordinate); //So this will convert coordinate to Point
    }
}
