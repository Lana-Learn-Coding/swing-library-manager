package io.lana.library.core.service;

import io.lana.library.core.model.user.User;
import io.lana.library.core.spi.PasswordEncoder;
import io.lana.library.core.spi.datacenter.UserDataCenter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {
    private UserDataCenter userDataCenter;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setup(UserDataCenter userDataCenter, PasswordEncoder passwordEncoder) {
        this.userDataCenter = userDataCenter;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createUser(User user) {
        if (StringUtils.isBlank(user.getUsername()) || user.getUsername().length() < 3) {
            throw new ServiceException("Username must not blank, and at least 3 character");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            throw new ServiceException("Please enter password");
        }
        if (StringUtils.isNotBlank(user.getEmail()) && existsByEmail(user.getEmail())) {
            throw new ServiceException("Email already exited");
        }
        if (StringUtils.isNotBlank(user.getPhoneNumber()) && existsByPhoneNumber(user.getPhoneNumber())) {
            throw new ServiceException("Phone number already exited");
        }
        if (existsByUsername(user.getUsername())) {
            throw new ServiceException("Username already existed");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDataCenter.save(user);
    }

    @Override
    public void updateUser(User user) {
        User updated = userDataCenter.findById(user.getId());
        if (StringUtils.isBlank(user.getUsername()) || user.getUsername().length() < 3) {
            throw new ServiceException("Username must not blank, and at least 3 character");
        }
        if (StringUtils.isNotBlank(user.getEmail())
            && !user.getEmail().equals(updated.getEmail())
            && existsByEmail(user.getEmail())) {
            throw new ServiceException("Email already exited");
        }
        if (!user.getUsername().equals(updated.getUsername())
            && existsByUsername(user.getUsername())) {
            throw new ServiceException("Username already exited");
        }
        if (StringUtils.isNotBlank(user.getPhoneNumber()) &&
            !user.getPhoneNumber().equals(updated.getPhoneNumber())
            && existsByPhoneNumber(user.getPhoneNumber())) {
            throw new ServiceException("Phone number already exited");
        }
        if (StringUtils.isNotBlank(user.getPassword())) {
            updated.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        updated.setName(user.getName());
        updated.setEmail(user.getEmail());
        updated.setPhoneNumber(user.getPhoneNumber());
        updated.setUsername(user.getUsername());
        updated.setPermissions(user.getPermissions());
        userDataCenter.update(updated);
    }

    @Override
    public void deleteUser(User user) {
        userDataCenter.delete(user);
    }


    private boolean existsByUsername(String username) {
        return userDataCenter.stream().anyMatch(user -> username.equals(user.getUsername()));
    }

    private boolean existsByPhoneNumber(String phoneNumber) {
        return userDataCenter.stream().anyMatch(user -> phoneNumber.equals(user.getPhoneNumber()));
    }

    private boolean existsByEmail(String email) {
        return userDataCenter.stream().anyMatch(user -> email.equals(user.getEmail()));
    }
}
