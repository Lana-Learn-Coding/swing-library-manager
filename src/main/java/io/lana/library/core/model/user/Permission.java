package io.lana.library.core.model.user;

import io.lana.library.core.model.base.Named;
import io.lana.library.core.model.base.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Objects;

@Table(name = "permission")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Permission implements Named {
    public static final Permission BOOK_MANAGE = new Permission("BOOK_MANAGE");
    public static final Permission READER_MANAGE = new Permission("READER_MANAGE");
    public static final Permission BORROWING_MANAGE = new Permission("BORROWING_MANAGE");
    public static final Permission USER_MANAGE = new Permission("USER_MANAGE");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Permission(String name) {
        setName(name);
    }

    @NaturalId
    @Column(nullable = false, unique = true)
    private String name;

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedEntity)) return false;
        NamedEntity named = (NamedEntity) o;
        return Objects.equals(getName(), named.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
