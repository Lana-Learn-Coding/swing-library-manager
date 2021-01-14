package io.lana.library.core.spi.repo;

import io.lana.library.core.model.user.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepo extends JpaRepository<Permission, Integer> {
}
