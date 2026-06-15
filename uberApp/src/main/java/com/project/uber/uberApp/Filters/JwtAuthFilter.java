package com.project.uber.uberApp.Filters;


import com.project.uber.uberApp.Entities.User;
import com.project.uber.uberApp.services.UserService;
import com.project.uber.uberApp.services.implementations.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserService userService; //Note -> This UserService should Implement UserDetailsService
    // In Spring, @Qualifier is used alongside @Autowired to resolve ambiguity when multiple beans of the
    // same type exist. It tells Spring exactly which bean to inject .
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver ;



    //We have wrapped our method inside try-catch to handle the exceptions like if we are not able to get JWT token
    //then it will throw the exception and go to the catch block and many more exception can occur as well so it will
    //handle all those exceptions .

    //All of my API's will go through this requestFilter everytime.
    //Each of the filter inside the SecurityFilterChain has this "doFilterInternal()" method and this method comes from the
    //"OncePerRequestFilter" that's why we are extending from "OncePerRequestFilter".
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            //Fetching the JWT token from request.
            //The request can contain multiple types of headers.
            //Headers are basically key-value pairs , and the key for our token would be "Authorization".
            final String requestTokenHeader = request.getHeader("Authorization");


            //if we don't get JWT token inside the header.
            //The nomenclature for storing the token is "bearer JWTtoken"
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
                //move to the next filter inside the filterChain
                filterChain.doFilter(request, response);
                return;
            }


            //Extracting the JWT token as token is stored in way like "Bearer JWTtoken" so to get the JWTtoken we have to split using
            //"Bearer " so we will get an array of string where 0th index contains an empty String("") , and 1st index contains the JWT token
            //so take the 1st index value out of it .
           String token = requestTokenHeader.split("Bearer ")[1];
                          //OR
//          (?i) → case-insensitive (matches Bearer or bearer)
//          \\s+ → matches one or more spaces
//           .trim() → removes leading/trailing spaces
//            Now token will always correctly contain the JWT.
//            String token = requestTokenHeader.replaceFirst("(?i)^Bearer\\s+","").trim();


            //Taking the userId from the token
            Long userId = jwtService.getUserIdFromToken(token);


            //We are making sure that the Authentication object is not present inside the "SecurityContextHolder" already before
            //that is why we are using ...getAuthentication() == null
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userService.getUserFromId(userId);

                // creating an authentication
                // putting user inside the UsernamePasswordAuthenticationToken and we can get this user by doing ->
                // SecurityContextHolder.getContext().setAuthentication.getPrinciple();
                // We are passing the user Object as the principle , passing credentials as null as we already got the User from token means credentials were correct,
                // passing authority(i,e. user.getAuthorities()) so that SpringSecurity Authorization part can do its Job.
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());


//Now adding the user inside the SecurityContextHolder so "SecurityContextHolder takes in authentication object as input" that
//is why we are creating an authentication and this authentication contains the user.
//After adding user we can get the userdata(i.e. User) anywhere inside my application from the SecurityContextHolder.

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            //Moving the request to the next filter inside the filterChain & it is a necessary step.
            filterChain.doFilter(request, response);

            //now at the last add this custom filter inside the filterChain(i.e. Go to WebSecurityConfig ) and after adding the filter before
            //UsernamePasswordAuthenticationFilter so now the UsernamePasswordAuthenticationFilter will see that the Authentication object is already
            //present inside SecurityContextHolder so it will move the request to the next filter in the filter chain and so on .

        }catch (Exception e){
            //it takes 4 input arguments -> req,res,handler,exception
            handlerExceptionResolver.resolveException(request,response, null ,e);
        }
    }
}


