package com.project.uber.uberApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RiderDto {

    private Long id;
    private UserDto user;
    private Double rating;


}