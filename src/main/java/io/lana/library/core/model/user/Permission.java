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
}
