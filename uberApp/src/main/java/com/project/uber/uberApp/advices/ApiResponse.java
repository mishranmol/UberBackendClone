package com.project.uber.uberApp.advices;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {

    private LocalDateTime timestamp;

    private T data;

    private  ApiError error;


    //It will create timestamp for every response
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(T data) {
        this();//calling Default constructor
        this.data = data;
    }

    public ApiResponse(ApiError error) {
        this();//calling default constructor
        this.error = error;
    }
}