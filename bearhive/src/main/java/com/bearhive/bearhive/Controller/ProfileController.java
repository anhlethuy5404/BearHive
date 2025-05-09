package com.bearhive.bearhive.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Repository.UserRepository;
import com.bearhive.bearhive.Service.UserService;

@Controller
public class ProfileController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/info/{userId}")
    public String profile(@PathVariable Long userId, Model model) {
        User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("user", user);
        return "userinfo";
    }
}
