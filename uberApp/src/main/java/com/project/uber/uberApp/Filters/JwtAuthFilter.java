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
    private final UserService userService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver ;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {

            final String requestTokenHeader = request.getHeader("Authorization");

            //The nomenclature for storing the token is "bearer JWTtoken"
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
                //move to the next filter inside the filterChain
                filterChain.doFilter(request, response);
                return;
            }


           String token = requestTokenHeader.split("Bearer ")[1];


            Long userId = jwtService.getUserIdFromToken(token);


            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userService.getUserFromId(userId);


                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());


                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }


            filterChain.doFilter(request, response);

        }catch (Exception e){
            handlerExceptionResolver.resolveException(request,response, null ,e);
        }
    }
}


