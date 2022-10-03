package com.edu.ulab.app.repository;

import com.edu.ulab.app.entity.Person;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Person, Long> {

    /*
    User has books - book - started - comited status - other logic
    User has books - book - in progress
    User has books - book - finished
     */

    @Query("select p from Person p where p.id = :id")
    Optional<Person> findByIdForUpdate(long id);

    @NotNull List<Person> findAll();

}
