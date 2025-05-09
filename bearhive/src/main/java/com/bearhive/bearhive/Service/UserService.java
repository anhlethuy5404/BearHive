package com.bearhive.bearhive.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Repository.UserRepository;
import com.bearhive.bearhive.Util.AvatarUtil;

import jakarta.servlet.http.HttpSession;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user, String password) {
        Optional<User> existedUser = userRepository.findByEmail(user.getEmail());
        if (existedUser.isPresent()) {
            return null;
        }
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }
    
    public User loginUser(String email, String password, HttpSession session) {
        Optional<User> existedUser = userRepository.findByEmail(email);
        if (existedUser.isPresent() && passwordEncoder.matches(password, existedUser.get().getPassword())) {
            User user = existedUser.get();
            if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
                String randomAvt = AvatarUtil.getRandomAvatar();
                user.setAvatar(randomAvt);
                userRepository.save(user);
            }
            session.setAttribute("loggedUser", user);
            return user;
        }
        return null;
    }

    public void updatePassword(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

}
