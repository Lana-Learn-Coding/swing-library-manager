package io.lana.library.core.model.book;

import io.lana.library.core.model.Reader;
import io.lana.library.core.model.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "book")
@SQLDelete(sql = "UPDATE book SET is_deleted = true WHERE id = ?")
public class Book extends BaseEntity {
    private Integer condition;

    private String image;

    private String note;

    private String position;

    @Column(name = "is_deleted")
    private Boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "meta_id")
    private BookMeta meta;

    @ManyToOne
    @JoinColumn(name = "storage_id")
    private Storage storage;

    @ManyToMany(mappedBy = "books", fetch = FetchType.EAGER)
    private Set<Ticket> tickets = new HashSet<>();

    public String getPosition() {
        return StringUtils.isBlank(position) ? "Not specified" : position;
    }

    @Transient
    public boolean isBorrowed() {
        return tickets.stream().anyMatch(Ticket::isBorrowing);
    }

    @Transient
    public boolean notBorrowed() {
        return tickets.stream().anyMatch(Ticket::isBorrowing);
    }

    @Transient
    public Ticket getBorrowingTicket() {
        return tickets.stream()
            .filter(Ticket::isBorrowing)
            .findFirst()
            .orElse(null);
    }

    @Transient
    public Reader getBorrower() {
        return tickets.stream()
            .filter(Ticket::isBorrowing)
            .findFirst()
            .map(Ticket::getBorrower)
            .orElse(null);
    }

    @Transient
    public String getBorrowerName() {
        Reader borrower = getBorrower();
        if (borrower != null) {
            return borrower.getName();
        }
        return "";
    }

    @Transient
    public String getTitle() {
        return getMeta().getTitle();
    }

    @Transient
    public String getBorrowerPhone() {
        Reader borrower = getBorrower();
        if (borrower != null) {
            return borrower.getPhoneNumber();
        }
        return "";
    }

    @Transient
    public LocalDate getDueDate() {
        Ticket borrowing = getBorrowingTicket();
        if (borrowing != null) {
            return borrowing.getDueDate();
        }
        return null;
    }

    @Transient
    public LocalDate getBorrowedDate() {
        Ticket borrowing = getBorrowingTicket();
        if (borrowing != null) {
            return borrowing.getBorrowedDate();
        }
        return null;
    }

    @Transient
    public String getStorageName() {
        if (isNotDeleted()) {
            return getStorage().getName();
        }
        return "deleted";
    }

    @Transient
    public boolean isNotDeleted() {
        return !getDeleted();
    }

    @Override
    public String toString() {
        return getIdString() + " - " + getTitle();
    }
}
