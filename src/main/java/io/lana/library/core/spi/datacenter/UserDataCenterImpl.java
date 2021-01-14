package io.lana.library.core.spi.datacenter;

import io.lana.library.core.model.user.User;
import io.lana.library.core.spi.datacenter.base.AbstractRepositoryDataCenter;
import io.lana.library.core.spi.datacenter.base.DataChange;
import io.lana.library.core.spi.repo.PermissionRepo;
import io.lana.library.core.spi.repo.UserRepo;
import io.lana.library.utils.WorkerUtils;
import org.springframework.stereotype.Component;

@Component
public class UserDataCenterImpl extends AbstractRepositoryDataCenter<Integer, User> implements UserDataCenter {
    private final PermissionRepo permissionRepo;

    public UserDataCenterImpl(UserRepo repo, PermissionRepo permissionRepo) {
        super(repo);
        this.permissionRepo = permissionRepo;
    }

    @Override
    public void save(User entity) {
        entity.getPermissions().forEach(permissionRepo::save);
        super.save(entity);
    }

    @Override
    public void update(User entity) {
        User oldData = internalData.get(entity.getId());
        if (oldData == null) {
            throw new RuntimeException("ID not found: " + entity.getId());
        }
        updateSink.tryEmitNext(new DataChange<>(oldData, entity));
        internalData.put(entity.getId(), entity);
        WorkerUtils.runAsync(() -> {
            entity.getPermissions().forEach(permissionRepo::save);
            repo.save(entity);
            updatedSink.tryEmitNext(new DataChange<>(oldData, entity));
        });
    }

    @Override
    public void updateAll(Iterable<User> entities) {
        entities.forEach(entity -> {
            User oldData = internalData.get(entity.getId());
            if (oldData == null) {
                throw new RuntimeException("ID not found: " + entity.getId());
            }
            internalData.put(entity.getId(), entity);
            updateSink.tryEmitNext(new DataChange<>(oldData, entity));
        });

        WorkerUtils.runAsync(() -> {
            entities.forEach(entity -> {
                User oldData = internalData.get(entity.getId());
                entity.getPermissions().forEach(permissionRepo::save);
                repo.save(entity);
                updatedSink.tryEmitNext(new DataChange<>(oldData, entity));
            });
        });
    }
}
