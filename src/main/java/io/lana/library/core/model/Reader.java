package io.lana.library.core.model;

import io.lana.library.core.model.base.BaseEntity;
import io.lana.library.core.model.book.Book;
import io.lana.library.core.model.book.Ticket;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Setter
@Getter
@Table(name = "reader")
public class Reader extends BaseEntity {
    @Column(unique = true)
    private String email;

    @Column(unique = true, name = "phone_number")
    private String phoneNumber;

    private String name;

    private String address;

    private String avatar;

    private Boolean gender;

    @Column(name = "borrow_limit")
    private Integer limit;

    private LocalDate birth;

    @OneToMany(mappedBy = "borrower", fetch = FetchType.EAGER)
    private Set<Ticket> tickets = new HashSet<>();

    @Transient
    public String getGenderString() {
        return getGender() ? "Male" : "Female";
    }

    @Transient
    public Set<Book> getBorrowedBooks() {
        return tickets.stream()
            .filter(Ticket::isBorrowing)
            .flatMap(ticket -> ticket.getBooks().stream())
            .filter(Book::isNotDeleted)
            .collect(Collectors.toSet());
    }

    @Transient
    public Integer getBorrowedBookCount() {
        return (int) tickets.stream()
            .filter(Ticket::isBorrowing)
            .flatMap(ticket -> ticket.getBooks().stream())
            .filter(Book::isNotDeleted)
            .count();
    }
}
