package com.project.uber.uberApp.Filters;


import com.project.uber.uberApp.Entities.User;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.UserRepository;
import com.project.uber.uberApp.services.UserService;
import com.project.uber.uberApp.services.implementations.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final UserService userService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver ;


    private String secretKey= "anjdnjadksjanfdkjks556fnsabfdanflwqdkq78qtewuk76dfsmknjnscsdjns23dwjnj0d";

    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }



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


            User user1 = userRepository.findById(userId).
                    orElseThrow(() -> new ResourceNotFoundException("User with given Id not found: "+userId));

            Instant lastlogoutTime = user1.getLogoutTime();

            //Inside claims we have stored the getIssuedAt Time while generating the AccessToken(JWT).
            Claims claims = Jwts.parserBuilder()//It's method provided by the JJWT library to create and configure a JWT parser before parsing a token
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Instant issuedAt = claims.getIssuedAt().toInstant();

            if(lastlogoutTime!=null && lastlogoutTime.isAfter(issuedAt)){
                throw new JwtException("Token issued before logout, invalid");
            }


            if (SecurityContextHolder.getContext().getAuthentication() == null) {
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


