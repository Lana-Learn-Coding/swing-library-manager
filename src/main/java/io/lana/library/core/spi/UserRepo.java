package io.lana.library.core.spi;

import io.lana.library.core.model.user.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends PagingAndSortingRepository<User, Integer> {
    Optional<User> findByUsernameEquals(String username);

    List<User> findAllByOrderByUpdatedAtDesc();
}
