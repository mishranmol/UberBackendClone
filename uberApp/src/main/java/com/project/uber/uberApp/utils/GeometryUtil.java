package com.project.uber.uberApp.utils;

import com.project.uber.uberApp.dto.PointDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryUtil {

    public static Point createPoint(PointDto pointDto){
        //To convert PointDto to Point will use GeometryFactory.
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),4326);
        //pointDto contains the coordinates array , 0->Longitude,[1]-> Latitude
        Coordinate coordinate = new Coordinate(pointDto.getCoordinates()[0],pointDto.getCoordinates()[1]);
        return geometryFactory.createPoint(coordinate);
    }
}
