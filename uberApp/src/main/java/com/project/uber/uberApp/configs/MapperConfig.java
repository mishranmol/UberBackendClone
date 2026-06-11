package com.project.uber.uberApp.configs;

import com.project.uber.uberApp.dto.PointDto;
import com.project.uber.uberApp.utils.GeometryUtil;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();

        //Below change is due to implementation of rideRequest method , there modelmapper was not able to convert Point into PointDto
        //and vice-versa.
        //setConverter() is not a Spring Boot annotation or keyword. It is generally a setter method used to inject or configure a
        //converter object that transforms data from one type to another.
        //context is something that contains information about something.
        //Converting PointDto to Point
        modelMapper.typeMap(PointDto.class,Point.class).setConverter(context -> {
            PointDto pointDto = context.getSource();
            return GeometryUtil.createPoint(pointDto);
        });
        //Converting Point to PointDto
        modelMapper.typeMap(Point.class,PointDto.class).setConverter(context -> {
            Point point= context.getSource();
            double coordinates[] = {
                    point.getX(),
                    point.getY()
            };
            return new PointDto(coordinates);
        });

        return modelMapper;
    }
}
