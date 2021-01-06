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
@Getter
@Setter
@Table(name = "series")
public class Series extends NamedEntity {
    @OneToMany(mappedBy = "series")
    private Set<BookMeta> books = new HashSet<>();
}
