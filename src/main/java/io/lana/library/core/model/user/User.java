package io.lana.library.core.model.user;

import io.lana.library.core.model.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "user")
@Entity
@Getter
@Setter
public class User extends BaseEntity {
}
