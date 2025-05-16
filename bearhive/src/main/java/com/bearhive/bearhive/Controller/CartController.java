package com.bearhive.bearhive.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Model.UserCourse;
import com.bearhive.bearhive.Repository.UserRepository;
import com.bearhive.bearhive.Service.UserCourseService;

@Controller
public class CartController {
    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/cart")
    public String showCart(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<UserCourse> cartItems = new ArrayList<>();

        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user email"));
            cartItems = userCourseService.findByUserIdAndStatus(user.getId(), "CART");
        }

        model.addAttribute("cartItems", cartItems);
        return "cart";
    }

    @PostMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user email"));
            UserCourse userCourse = userCourseService.findById(id)
                    .filter(uc -> uc.getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid cart item ID"));
            userCourseService.delete(userCourse);
            redirectAttributes.addFlashAttribute("message", "Course removed from cart!");
        }
        return "redirect:/cart";
    }
}
