package com.project.uber.uberApp.repositories;

import com.project.uber.uberApp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    //We don't have to write the query here , as Spring Data JPA can automatically generate queries.Methods like findByEmail()
    //follow naming conventions, so Spring creates the corresponding SQL/JPQL query at runtime without requiring an explicit
    //@Query annotation.

    //Optional<User> is used because a user may or may not exist for the given email.If no user is found, it returns null ,
    // which can cause "NullPointerException".
    Optional<User> findByEmail(String email);

}
