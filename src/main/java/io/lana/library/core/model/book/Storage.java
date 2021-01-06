package io.lana.library.core.model.book;

import io.lana.library.core.model.base.NamedEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "storage")
public class Storage extends NamedEntity {
    @OneToMany(mappedBy = "storage")
    private Set<Book> books = new HashSet<>();
}
