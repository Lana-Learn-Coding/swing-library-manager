package io.lana.library.core.model.base;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
@Getter
@Setter
public class NamedEntity extends BaseEntity implements Named {
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
