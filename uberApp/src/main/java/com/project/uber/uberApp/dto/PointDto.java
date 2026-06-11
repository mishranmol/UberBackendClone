package com.project.uber.uberApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor //This is req. by jackson to create objects
//This point Dto will contain some Co-ordinates
public class PointDto {
    private double[] coordinates;
    private String type="Point";//This means the above coordinates belong to Geo-Spatial Point.

    public PointDto(double[] coordinates){
        this.coordinates=coordinates;
    }

}
