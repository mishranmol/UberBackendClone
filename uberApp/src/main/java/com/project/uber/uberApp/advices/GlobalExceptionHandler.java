package com.project.uber.uberApp.advices;


import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.exceptions.RuntimeConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Since we are using builder pattern hence we have used @Builder in ApiError
    @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException exception){
            ApiError apiError = ApiError.builder().
                    status(HttpStatus.NOT_FOUND).
                    message(exception.getMessage())
                    .build();
            return buildErrorResponseEntity(apiError);
        }


    @ExceptionHandler(RuntimeConflictException.class)
        public ResponseEntity<ApiResponse<?>> handleRuntimeConflictException(RuntimeConflictException exception){
            ApiError apiError = ApiError.builder().
                    status(HttpStatus.CONFLICT).
                    message(exception.getMessage())
                    .build();
            return buildErrorResponseEntity(apiError);
        }

    private ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError){
        return new ResponseEntity<>(new ApiResponse<>(apiError) , apiError.getStatus());
    }
}
