package io.lana.library.core.model.book;

import io.lana.library.core.model.Reader;
import io.lana.library.core.model.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "book_borrowing")
public class BookBorrowing extends BaseEntity {
    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "borrowed_date")
    private LocalDate borrowedDate;

    private String note;

    @OneToMany(mappedBy = "borrowing")
    private Set<Book> books;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private Reader borrower;
}
