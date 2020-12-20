package io.lana.library.core.model.book;

import io.lana.library.core.model.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "book")
public class Book extends BaseEntity {
    private Integer condition;

    private String image;

    private String note;

    @ManyToOne
    @JoinColumn(name = "meta_id")
    private BookMeta meta;

    @ManyToOne
    @JoinColumn(name = "storage_id")
    private Storage storage;

    @ManyToOne
    @JoinColumn(name = "book_borrowing_id")
    private BookBorrowing borrowing;
}
