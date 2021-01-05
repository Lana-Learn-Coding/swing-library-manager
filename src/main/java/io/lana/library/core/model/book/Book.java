package io.lana.library.core.model.book;

import io.lana.library.core.model.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "book")
public class Book extends BaseEntity {
    private Integer condition;

    private String image;

    private String note;

    private String position;

    @ManyToOne
    @JoinColumn(name = "meta_id")
    private BookMeta meta;

    @ManyToOne
    @JoinColumn(name = "storage_id")
    private Storage storage;

    @ManyToOne
    @JoinColumn(name = "book_borrowing_id")
    private BookBorrowing borrowing;

    public String getPosition() {
        return StringUtils.isBlank(position) ? "Not specified" : position;
    }

    @Transient
    public boolean isBorrowed() {
        return borrowing != null;
    }

    @Transient
    public String getBorrower() {
        if (isBorrowed()) {
            return getBorrowing().getBorrower().getName();
        }
        return "None";
    }

    @Transient
    public String getBorrowerPhone() {
        if (isBorrowed()) {
            return getBorrowing().getBorrower().getPhoneNumber();
        }
        return "None";
    }

    @Transient
    public LocalDate getBorrowedDate() {
        if (isBorrowed()) {
            return getBorrowing().getBorrowedDate();
        }
        return null;
    }

    @Transient
    public LocalDate getDueDate() {
        if (isBorrowed()) {
            return getBorrowing().getDueDate();
        }
        return null;
    }

    @Transient
    public String getStorageName() {
        return getStorage().getName();
    }
}
