package com.bearhive.bearhive.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Repository.UserRepository;
import com.bearhive.bearhive.Service.UserService;

import jakarta.servlet.http.HttpSession;



@Controller
public class UserController {
    @Autowired 
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/signup")
    public String getRegistrationPage(@ModelAttribute("user") User user, Model model) {
        return "signup";
    }
    
    @PostMapping("/signup")
    public String saveUser(@ModelAttribute("user") User user, Model model, @RequestParam("password") String password) {
        User savedUser = userService.registerUser(user, password);
        if (savedUser == null) {
            model.addAttribute("message", "Email đã được đăng kí!");
            return "signup";
        }
        else {
            model.addAttribute("message", "Đăng kí thành công!");
            return "signup";
        }
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginForm(@RequestParam("email") String email, @RequestParam("password") String password, RedirectAttributes redirectAttributes, HttpSession session) {
        User user = userService.loginUser(email, password, session);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Email hoặc mật khẩu không đúng!");
            return "redirect:/login";  
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);
        session.setAttribute("loggedUser", user);
        String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            session.removeAttribute("redirectAfterLogin"); 
            return "redirect:" + redirectUrl;
        }
        
        return "redirect:/home";   
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/home";  
    }
}
