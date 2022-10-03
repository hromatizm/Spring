package com.edu.ulab.app.repository;

import com.edu.ulab.app.entity.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends CrudRepository<Book, Long> {

    @Query("select b from Book b where b.id = :id")
    Optional<Book> findByIdForUpdate(long id);

    List<Book> findBooksByUserId(long id);

    @Transactional
    void deleteBooksByUserId(long id);
}
