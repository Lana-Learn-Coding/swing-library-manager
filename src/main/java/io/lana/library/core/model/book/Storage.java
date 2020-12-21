package io.lana.library.core.model.book;

import io.lana.library.core.model.base.BaseEntity;
import io.lana.library.core.model.base.Named;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "storage")
public class Storage extends BaseEntity implements Named {
    private String name;

    @OneToMany(mappedBy = "storage")
    private Set<Book> books;

    @Override
    public String toString() {
        return getName();
    }
}
