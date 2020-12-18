package io.lana.library.core.model;

import io.lana.library.core.model.book.Book;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Setter
@Getter
public class Reader {
    @Id
    @GeneratedValue
    private Integer id;

    @OneToMany
    private Set<Book> borrowedBooks;

    private String email;

    private String phoneNumber;

    private String name;

    private Integer limit;
}
