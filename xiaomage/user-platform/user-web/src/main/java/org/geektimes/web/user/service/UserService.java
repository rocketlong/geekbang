package org.geektimes.web.user.service;

import org.geektimes.web.user.domain.User;

import java.util.Collection;

public interface UserService {

    int save(User user);

    int deleteById(Long userId);

    int update(User user);

    User getById(Long userId);

    User getByNameAndPassword(String userName, String password);

    Collection<User> getAll();
}
