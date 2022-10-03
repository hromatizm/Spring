package com.edu.ulab.app.repository;

import com.edu.ulab.app.entity.Book;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select b from Book b where b.id = :id")
    Optional<Book> findByIdForUpdate(long id);

    List<Book> findBooksByPersonId(long id);

    @Transactional
    void deleteBooksByPersonId(long id);

    @NotNull List<Book> findAll();
}
