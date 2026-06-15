package com.project.uber.uberApp.services.implementations;


import com.project.uber.uberApp.Entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JWTService {

    //Note -> This @Value is coming from beans.factory.annotation.Value and not from lombok.
    @Value("${jwt.secretKey}")
    private String secretKey;

    //creating method to generate key , as signWith needs a key as input
    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }


    //Method used to generate the JWT(JSON WEB TOKEN). Make sure the user is coming from userEntity and not any other source.
    public String generateAccessToken(User user) {

        //Jwts is a class inside the JJWT library.JJWT is the library.
        //We are using Jwts class as we have added jjwt library dependency inside pom.xml.
        //builder is used to build the token.
        //subject  is something used to identify the user.
        //ID is Long so converting it to String using toString().
        //claims are key-value pairs and inside the claims we can add our data(payload) which we want to send.
        //setIssuedAt tells the time at what the JWT is created , each JWT must have issued & expire time as well.
        //setExpiration tells the time at which the JWT will expire.
        //new Date() gives you the current date and time.
        //1 sec -> 1000 Milliseconds , setting the expiration time of JWT after 10 mins.
        //signWith needs a key Object as input.
        //no such description about compact()

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 ))
                .signWith(getSecretKey())
                .compact();
    }





    public String generateRefreshToken(User user) {

        //Jwts is a class inside the JJWT library.JJWT is the library.
        //We are using Jwts class as we have added jjwt library dependency inside pom.xml.
        //We have removed claims from Refresh token because we don't want that Refresh token to act as access token .
        //Setting the expiration time of Refresh token to 6 months -> 1000*60*60*24*30*6
        //We will do 1000L and not just 1000 as Numeric overflow in expression 1000 * 60 * 60 * 24 * 30 * 6: integer multiplication will occur
        //So will implicitly cast to long .

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30 *6))//As it will numeric flow as typeCasted to long by 'L'.
                .signWith(getSecretKey())
                .compact();
    }


    public Long getUserIdFromToken(String token) {

        //We are using Jwts class as we have added jjwt library dependency inside pom.xml.
        //We are taking out the claims because claims contains the payload inside the JWT.
        //parsing the token means reading and extracting the information(claims) stored inside JWT.
        //Then we will verify the token with the secretKey we have hence using .setSigningKey(getSecretKey()) but
        //in Anuj-bhaiya video he used  .verify(getSecretKey()) but this method is giving error when using

        Claims claims = Jwts.parserBuilder()//It's method provided by the JJWT library to create and configure a JWT parser before parsing a token
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        //As we have stored the ID of the user inside "Subject(.setSubject(user.getId().toString()))" when generating the token so for fetching use .getSubject() .
        //Since the Id was stored in the form of String when we were creating the token so converting String to Long hence used Long.valueof();
        return Long.valueOf(claims.getSubject());
    }

}