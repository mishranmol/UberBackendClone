package com.project.uber.uberApp.advices;


import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.exceptions.RuntimeConflictException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.core.AuthenticationException;

@RestControllerAdvice//It acts as a global exception handler for the entire application.
public class GlobalExceptionHandler {

    //Since we are using builder pattern hence we have used @Builder in ApiError
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException exception) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(RuntimeConflictException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeConflictException(RuntimeConflictException exception) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .message(exception.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    //Note below 3 Points carefully while handling SpringSecurity Exceptions.

    //1st
    //You throw your own custom exceptions (like ResourceNotFoundException), but SpringSecurity and JWT library throw their
    //own exceptions automatically when authentication, authorization, or token validation fails. Your @ExceptionHandler
    //methods simply catch and format those exceptions into a API response.

    //2nd
    //"AuthenticationException" and "AccessDeniedException" are SpringSecurity exceptions used during authentication and
    //authorization. "JwtException" is provided by the JJWT library and is thrown when there is a problem with JWT validation
    //such as token expiration, invalid signature, or malformed token.SpringSecurity and JWT library throw these Exceptions internally,
    //we don't have to throw these manually , just what we are doing below is that mapping the response of these exceptions into our own ApiResponse.

    //3rd
    //Even if we don't add @ExceptionHandler for these three exceptions:AuthenticationException,AccessDeniedException,JwtException inside your GlobalExceptionHandler,
    //then also these exceptions will still be thrown by Spring Security or the JWT library.The difference is only how the response is returned to the client.
    //Without our handlers SpringSecurity and JWT library will handle them itself and returns its default response , but if declared inside GlobalExcepHandler
    //then it gets it converted into your custom format.


    //This Exception is for Login/authentication failure , means Login Credentials Incorrect.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthenticationException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    //This Exception is when User is authenticated but lacks required role.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    //This is for Invalid/expired JWT token.
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<?>> handleJwtException(JwtException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(Exception exception) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }


    private ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getStatus());
    }

}
