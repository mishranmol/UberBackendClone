package com.project.uber.uberApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PointDto {
    private double[] coordinates;
    private String type="Point";//This means the above coordinates belong to Geo-Spatial Point.

    public PointDto(double[] coordinates){
        this.coordinates=coordinates;
    }

}
