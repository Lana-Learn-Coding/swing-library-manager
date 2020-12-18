package io.lana.library.core.model.book;

import io.lana.library.core.model.Reader;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Book {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private BookMeta meta;

    @ManyToOne
    private Storage storage;

    @ManyToOne
    private Reader borrower;

    private Integer condition;

    private String image;

    private String note;
}
