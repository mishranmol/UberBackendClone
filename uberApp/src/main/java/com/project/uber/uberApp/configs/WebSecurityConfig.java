package com.project.uber.uberApp.configs;

import com.project.uber.uberApp.Filters.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity //It's a SpringSecurity annotation used to enable and configure web security in a Spring Boot application.
@EnableMethodSecurity(securedEnabled = true) //By default securedEnabled is false. //@EnableMethodSecurity tells Spring Security
//to check role/permission annotations like @PreAuthorize on methods before allowing them to execute.
public class WebSecurityConfig {

    //Brief of how SpringSecurity Works : Anuj-Bhaiya Language
    //We have SecurityFilterChain and all the API requests goes through this SecurityFilterChain & then the SecurityFilters in those
    //chain give the Authenticated Authentication Back to the SpringSecurityContextHolder & if one of the filter's does this job then
    //all the other filters will simply call the next Filter in the filterChain.
                                               // OR
    //ChatGPT Language:
    //All API requests pass through the SecurityFilterChain. Each filter can perform tasks such as authentication and authorization.
    //If a filter successfully authenticates the user, it stores the "Authentication object" in the "SecurityContextHolder".
    //After that,the request continues through the remaining filters in the chain until it reaches controller,if authorized.

    private final String[] PUBLIC_ROUTES = {"/auth/**"}; // "auth/**" is an Ant-style URL pattern. It means Match all endpoints that start with "/auth/".
    //Example: /auth/login , /auth/signup

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){

        //Configuring the SecurityFilterChain.

        //As will be doing Authentication via Token(JWT) & JWT is "Stateless" which means our server don't have to hold any kind of information about each Authenticated
        //User,each API call is capable of identifying who the calling user is.Hence, doing SessionCreationPolicy=STATELESS means SpringSecurity will not maintain any session.

        //Disable CSRF (Cross-Site Request Forgery) protection in Spring Security.Why do we disable it?
        //For REST APIs (especially JWT-based authentication), we usually don't use sessions and cookies for authentication.So CSRF
        //protection is unnecessary & is disabled.

        //authorizeHttpRequests() ->  Is used to define which users can access which URLs/endpoints.
        //permitAll() → Anyone can access. authenticated() → User must be logged in.
        httpSecurity
                .sessionManagement(sessionConfig ->
                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(PUBLIC_ROUTES).permitAll() //All endpoints listed in PUBLIC_ROUTES can be accessed by anyone, even if they are not logged in/Authenticated.
                                .anyRequest().authenticated() //Any request not matched by previous reqMatchers rules must be from an authenticated/logged-in user.
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        return httpSecurity.build(); //Build returns SecurityFilterChain

    }



    //Creating "AuthenticationManager" Object -> Inside login() will be taking email & password & pass these 2 things inside AuthenticationManager
    //and then AuthenticationManager will take these 2, and it will hit the filter(JWTFilter) & from this filter we are going to check that
    //whether user exists inside DB or not and if credentials are correct then will generate the userToken.
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration){
        return configuration.getAuthenticationManager();
    }

}
