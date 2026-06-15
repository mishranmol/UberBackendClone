package com.project.uber.uberApp.Entities;

import com.project.uber.uberApp.Entities.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="app_user") //Giving table name as "app_user" and not as "user" because PostgresSQL uses a UserSchema and for that is uses User Table so conflict may arise.
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
// We can use GenerationType.SEQUENCE , as PostgreSQL supports SEQUENCE , what it will do is it creates a new table for just
// handling the ID's but this approach is used when we want to have some Batch Operation , as the IDENTITY will fail.
    private Long id;
    private String name;

    //Adding a Constraint
    @Column(unique = true)
    //Is to ensure that no two users can have the same email address in the database.The database will reject
    //the duplicate record and throw an error(i.e --> Unique Constraint Violation) because duplicate email values are not allowed.
    private String email;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER) //@ElementCollection is used to store a collection of non-entity values
    // (such as Enum, String, Integer, etc.) in a separate table.By default its LAZY , FetchType.LAZY means the collection is loaded from the DB only
    // when it is accessed, which helps improve performance. FetchType.EAGER means it will fetched immediately as soon as we fetch user.
    @Enumerated(EnumType.STRING) //Stores enum values as strings in the database rather than numbers.Stores as USER,ADMIN rather than 0,1
    // If we give EnumType.ORDINAL then it will store as 0,1
    private Set<Role> roles;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return email;
    }
}

//Explanation of  public Collection<? extends GrantedAuthority> getAuthorities() method below :
/*
This code is converting your roles into Spring Security authorities.
        return roles.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
        .collect(Collectors.toSet());

Let's break it down step by step.

Suppose your user has : roles = [RIDER, DRIVER]

Step 1: roles.stream()
Converts the collection into a stream so we can process each role.
        [RIDER, DRIVER]
             ↓
        Stream<RIDER, DRIVER>


Step 2: .map(...)
For each role, create a SimpleGrantedAuthority.
        role -> new SimpleGrantedAuthority("ROLE_" + role.name())

For RIDER : new SimpleGrantedAuthority("ROLE_RIDER")

For DRIVER : new SimpleGrantedAuthority("ROLE_DRIVER")

Now the stream contains:[
SimpleGrantedAuthority("ROLE_RIDER"),
SimpleGrantedAuthority("ROLE_DRIVER")
]


Step 3: .collect(Collectors.toSet())
Collect all authorities into a Set.

Result:Set<GrantedAuthority> authorities = Set.of(
        new SimpleGrantedAuthority("ROLE_RIDER"),
        new SimpleGrantedAuthority("ROLE_DRIVER"));

Why add "ROLE_"?

Spring Security expects roles in the format:
ROLE_ADMIN
ROLE_USER
ROLE_DRIVER
ROLE_RIDER

So when you write : .hasRole("DRIVER")

Spring Security actually checks for : ROLE_DRIVER behind the scenes.


One-line summary -> This code converts the user's roles (e.g., RIDER, DRIVER) into Spring Security authorities (ROLE_RIDER, ROLE_DRIVER) and returns them as a Set<GrantedAuthority>.

 */