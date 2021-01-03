package io.lana.library.core.model;

import io.lana.library.core.model.base.BaseEntity;
import io.lana.library.core.model.book.BookBorrowing;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    private Integer limit;

    private LocalDate birth;

    @OneToMany(mappedBy = "borrower")
    private Set<BookBorrowing> borrowedBooks = new HashSet<>();

    @Transient
    public String getGenderString() {
        return getGender() ? "M" : "F";
    }

    @Transient
    public Integer getBorrowedBookCount() {
        return (int) borrowedBooks.stream()
            .mapToLong(bookBorrowing -> bookBorrowing.getBooks().size())
            .sum();
    }
}
