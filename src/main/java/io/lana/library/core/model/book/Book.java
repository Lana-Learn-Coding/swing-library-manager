package io.lana.library.core.model.book;

import io.lana.library.core.model.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
}
