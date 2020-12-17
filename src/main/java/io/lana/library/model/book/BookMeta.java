package io.lana.library.model.book;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
public class BookMeta {
    @Id
    @GeneratedValue
    private Integer id;

    @OneToMany
    private Set<Book> books;

    @ManyToOne
    private Category category;

    private String title;

    private String author;

    private String publisher;

    private Integer year;
}
