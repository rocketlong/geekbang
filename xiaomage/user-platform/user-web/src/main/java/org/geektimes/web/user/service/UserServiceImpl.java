package org.geektimes.web.user.service;

import org.geektimes.web.user.domain.User;
import org.geektimes.web.user.repository.UserRepository;

import javax.annotation.Resource;
import java.util.Collection;

public class UserServiceImpl implements UserService {

    @Resource(name = "bean/DatabaseUserRepository")
    private UserRepository userRepository;

    @Override
    public int save(User user) {
        return userRepository.save(user);
    }

    @Override
    public int deleteById(Long userId) {
        return userRepository.deleteById(userId);
    }

    @Override
    public int update(User user) {
        return userRepository.update(user);
    }

    @Override
    public User getById(Long userId) {
        return userRepository.getById(userId);
    }

    @Override
    public User getByNameAndPassword(String userName, String password) {
        return userRepository.getByNameAndPassword(userName, password);
    }

    @Override
    public Collection<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.getByEmail(email);
    }

}
