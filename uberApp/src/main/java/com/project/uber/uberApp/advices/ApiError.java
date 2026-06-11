package com.project.uber.uberApp.advices;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;


//This will come when API doesn't work or gives some Error
//Used @Data so that we can use getters and setters
@Data
@Builder
public class ApiError {

    private HttpStatus status;

    private String message;

}