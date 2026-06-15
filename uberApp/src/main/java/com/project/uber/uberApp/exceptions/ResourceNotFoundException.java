package com.project.uber.uberApp.exceptions;

//What is this class?
//This is a custom exception.You create it when you want to throw a meaningful error like:throw new ResourceNotFoundException
//("User not found with id: 10");
//We extend Exception so that this class becomes a valid exception that can be thrown, caught, and handled by Java and Spring. Without
//extending Exception (or another subclass of Throwable), throw new ResourceNotFoundException(...) will not compile.
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String s){
        super(s);
    }
}



/*
How Custom Exceptions and Global Exception Handler Work in Spring Boot

Suppose inside a Service or Controller we write: throw new ResourceNotFoundException("Driver not found");

The moment this line executes, Java creates an object of our custom exception class ResourceNotFoundException.

public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String s) {
        super(s);
    }
}

The constructor is called with: "Driver not found" & Inside the constructor, we call: super(s);

which passes the message to the parent Exception class. The parent class stores this message internally so that later we can retrieve it using:
exception.getMessage();

Spring then starts looking for a method that can handle ResourceNotFoundException.Since we have a global exception handler:
@RestControllerAdvice
public class GlobalExceptionHandler{

@ExceptionHandler(ResourceNotFoundException.class)

Spring automatically routes the exception to this method: handleResourceNotFound(ResourceNotFoundException exception)

Now the exception object contains the message that was stored earlier through super(s).Inside the handler, we extract the message:
exception.getMessage() , which returns: Driver not found

Using this message, we create an ApiError object:

ApiError apiError = ApiError.builder()
        .status(HttpStatus.NOT_FOUND)
        .message(exception.getMessage())
        .build();

Finally, the Global Exception Handler converts this error into a proper HTTP response and sends it back to the client.

So instead of the application crashing or showing a long stack trace, Postman receives a clean and structured error response.
*/














/* BELOW IS EXPLANATION FOR , WHEN TO USE THROWS IN METHOD SIGNATURE AND WHEN NOT TO USE IT IN METHOD SIGNATURE.

If you don't want to write throws keyword everywhere in the method signature , then your custom exception should extend from: RuntimeException

Example:
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

Then you can freely write below without adding throws keyword in the Method Signature:
return userRepository.findById(id)
        .orElseThrow(() ->
                new ResourceNotFoundException("User not found"));

without: throws ResourceNotFoundException in the method signature.

Why?
Java exceptions are of two types:
1. Checked Exceptions -> Extend from: Exception ,
 Example: IOException, SQLException .

So it Must be:
	• Caught using try-catch, OR
	• Declared using throws Keyword

2. Unchecked Exceptions -> Extend from: RuntimeException
Example: NullPointerException, IllegalArgumentException

So there is No need to:
	• Catch them
	• Declare them with throws

In Spring Boot
Custom business exceptions are usually created like:

public class ResourceNotFoundException extends RuntimeException
public class DriverNotAvailableException extends RuntimeException
public class RideAlreadyCancelledException extends RuntimeException

and handled globally using:

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(
          ResourceNotFoundException ex) {
        …
		…….
		………..
    }
}
This is the most common and recommended approach in Spring Boot projects.

 */