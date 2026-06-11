package com.project.uber.uberApp.Entities;

import com.project.uber.uberApp.Entities.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="app_user")
public class User {

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

    @ElementCollection(fetch = FetchType.LAZY) //@ElementCollection is used to store a collection of non-entity values
    // (such as Enum, String, Integer, etc.) in a separate table. FetchType.LAZY means the collection is loaded from the DB only
    // when it is accessed, which helps improve performance.
    @Enumerated(EnumType.STRING) //Stores enum values as strings in the database rather than numbers.Stores as USER,ADMIN rather than 0,1
    // If we give EnumType.ORDINAL then it will store as 0,1
    private Set<Role> roles;


}
