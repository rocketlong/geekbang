package org.geektimes.web.user.service;

import org.geektimes.web.user.domain.User;

import java.util.Collection;

public class UserServiceImpl implements UserService {

    @Override
    public int save(User user) {
        return 0;
    }

    @Override
    public int deleteById(Long userId) {
        return 0;
    }

    @Override
    public int update(User user) {
        return 0;
    }

    @Override
    public User getById(Long userId) {
        return null;
    }

    @Override
    public User getByNameAndPassword(String userName, String password) {
        return null;
    }

    @Override
    public Collection<User> getAll() {
        return null;
    }

}
