package io.lana.library.core.service;

import io.lana.library.core.model.user.User;

public interface UserService {
    void createUser(User user);

    void updateUser(User user);

    void deleteUser(User user);
}
