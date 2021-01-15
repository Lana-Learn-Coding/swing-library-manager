package io.lana.library.core.model.user;

import io.lana.library.core.model.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Table(name = "backend_user")
@Entity
@Getter
@Setter
public class User extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Column(unique = true, name = "phone_number")
    private String phoneNumber;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "backend_user_permission",
        joinColumns = @JoinColumn(name = "backend_user_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "permission_id", nullable = false)
    )
    private Set<Permission> permissions = new HashSet<>();

    @Transient
    public boolean hasPermission(Permission permission) {
        return getPermissions().contains(permission);
    }

    @Transient
    public boolean hasPermission(String permissionName) {
        return getPermissions().contains(new Permission(permissionName));
    }
}
