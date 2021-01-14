package io.lana.library.core.model.user;

import io.lana.library.core.model.base.Named;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
public class Permission implements Named {
    public static final Permission BOOK_MANAGE = new Permission(1, "BOOK_MANAGE");
    public static final Permission READER_MANAGE = new Permission(2, "READER_MANAGE");
    public static final Permission BORROWING_MANAGE = new Permission(3, "BORROWING_MANAGE");
    public static final Permission USER_MANAGE = new Permission(4, "USER_MANAGE");

    public Permission(String name) {
        setName(name);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
        if (!(o instanceof Permission)) return false;
        Permission named = (Permission) o;
        return Objects.equals(getName(), named.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
