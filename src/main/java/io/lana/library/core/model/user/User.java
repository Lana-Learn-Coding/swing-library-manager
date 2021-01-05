package io.lana.library.core.model.user;

import io.lana.library.core.model.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Table(name = "backend_user")
@Entity
@Getter
@Setter
public class User extends BaseEntity {
    private String name;

    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @Column(unique = true, name = "phone_number")
    private String phoneNumber;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "backend_user_permission",
        joinColumns = @JoinColumn(name = "backend_user_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    @Transient
    private List<String> getPermissionNames() {
        return permissions.stream().map(Permission::toString).collect(Collectors.toList());
    }
}
