package io.lana.library.core.model.book;

import io.lana.library.core.model.Reader;
import io.lana.library.core.model.base.BaseEntity;
import io.lana.library.utils.DateFormatUtils;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Table(name = "borrow_ticket")
@AllArgsConstructor
@NoArgsConstructor
public class Ticket extends BaseEntity {
    @With
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "borrowed_date", nullable = false)
    private LocalDate borrowedDate = LocalDate.now();

    @Column(name = "returned_date")
    private LocalDate returnedDate;

    @Column(name = "is_returned", nullable = false)
    private boolean returned = false;

    @Column(length = 1024)
    private String note;

    @With
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "book_borrow_ticket",
        joinColumns = @JoinColumn(name = "borrow_ticket_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "book_id", nullable = false)
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

    public void setReturned(boolean returned) {
        if (returned == this.returned) {
            return;
        }

        this.returned = returned;
        if (returned) {
            setReturnedDate(LocalDate.now());
            return;
        }
        setReturnedDate(null);
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
    public Set<Book> getBorrowingBooks() {
        return getBooks().stream().filter(Book::isNotDeleted).collect(Collectors.toSet());
    }

    @Transient
    public int getBooksCount() {
        return (int) getBooks().stream().filter(Book::isNotDeleted).count();
    }

    @Transient
    public boolean isOverDue() {
        return isBorrowing() && getDueDate().isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        String name = "Ticket " + getId() + " - from "
                      + DateFormatUtils.toDateString(borrowedDate)
                      + " to " + DateFormatUtils.toDateString(dueDate);
        if (isReturned()) {
            name += " returned at " + DateFormatUtils.toDateString(returnedDate);
        }
        return name;
    }
}
