package io.lana.library.core.model.book;

import io.lana.library.core.model.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "book_meta")
public class BookMeta extends BaseEntity {
    private String title;

    private String author;

    private String publisher;

    private String image;

    private Integer year;

    @OneToMany(mappedBy = "meta", fetch = FetchType.EAGER)
    private Set<Book> books;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;

    @Transient
    public String getCategoryName() {
        return getCategory() == null ? "None" : getCategory().getName();
    }

    @Transient
    public String getSeriesName() {
        return getSeries() == null ? "None" : getSeries().getName();
    }

    @Transient
    public String getAuthorName() {
        return StringUtils.isBlank(getAuthor()) ? "Unknown" : getAuthor();
    }

    @Transient
    public String getPublisherName() {
        return StringUtils.isBlank(getPublisher()) ? "Unknown" : getPublisher();
    }
}
