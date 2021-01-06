package io.lana.library.core.model.user;

import io.lana.library.core.model.base.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "permission")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Permission extends NamedEntity {
    public static final Permission BOOK_MANAGE = new Permission("BOOK_MANAGE");
    public static final Permission READER_MANAGE = new Permission("READER_MANAGE");
    public static final Permission BORROWING_MANAGE = new Permission("BORROWING_MANAGE");
    public static final Permission USER_MANAGE = new Permission("USER_MANAGE");

    public Permission(String name) {
        setName(name);
    }
}
