package io.lana.library.core.spi.datacenter;

import io.lana.library.core.model.user.User;
import io.lana.library.core.spi.datacenter.base.AbstractRepositoryDataCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class UserDataCenterImpl extends AbstractRepositoryDataCenter<Integer, User> implements UserDataCenter {
    public UserDataCenterImpl(JpaRepository<User, Integer> repo) {
        super(repo);
    }
}
