package com.project.uber.uberApp.advices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


//This will come when API doesn't work or gives some Error
//Used @Data so that we can use getters and setters
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private HttpStatus status;
    private String message;
}