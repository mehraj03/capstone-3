package org.yearup.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.yearup.models.User;
import org.yearup.repository.UserRepository;

import java.util.List;

@Service
public class UserService
{
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public List<User> getAll()
    {
        return userRepository.findAll();
    }

    public User getUserById(int userId)
    {
        return userRepository.findById(userId).orElse(null);
    }

    public User getByUserName(String username)
    {
        return userRepository.findByUsername(username);
    }

    public int getIdByUsername(String username)
    {
        User user = userRepository.findByUsername(username);
        return user != null ? user.getId() : -1;
    }

    public boolean exists(String username)
    {
        return userRepository.existsByUsername(username);
    }

    public User create(User user)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
