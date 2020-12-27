package io.lana.library.core.model.book;

import io.lana.library.core.model.base.BaseEntity;
import io.lana.library.core.model.base.Named;
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
@Table(name = "category")
public class Category extends BaseEntity implements Named {
    private String name;

    @OneToMany(mappedBy = "category")
    private Set<BookMeta> books = new HashSet<>();

    @Override
    public String toString() {
        return getName();
    }
}
