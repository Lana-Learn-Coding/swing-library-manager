package io.lana.library.model.book;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Setter
@Getter
public class Storage {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    @OneToMany
    private Set<Book> books;
}
