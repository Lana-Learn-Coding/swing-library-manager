package io.lana.library.core.model.book;

import io.lana.library.core.model.Reader;
import io.lana.library.core.model.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "borrow_ticket")
@AllArgsConstructor
@NoArgsConstructor
public class Ticket extends BaseEntity {
    @With
    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "borrowed_date")
    private LocalDate borrowedDate = LocalDate.now();

    @Column(name = "is_returned")
    private boolean returned = false;

    private String note;

    @With
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "book_borrow_ticket",
        joinColumns = @JoinColumn(name = "borrow_ticket_id"),
        inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> books = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private Reader borrower;

    public Ticket withBook(Book book) {
        Set<Book> books = new HashSet<>();
        books.add(book);
        return withBooks(books);
    }

    @Transient
    public boolean isBorrowing() {
        return !isReturned();
    }

    @Transient
    public int getOverDueDays() {
        if (isReturned() || LocalDate.now().isBefore(getDueDate())) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(getDueDate(), LocalDate.now());
    }

    @Transient
    public boolean isOverDue() {
        return isBorrowing() && getDueDate().isBefore(LocalDate.now());
    }
}
