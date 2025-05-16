package com.bearhive.bearhive.Advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.bearhive.bearhive.Model.CustomUserDetail;
import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Repository.UserRepository;
import com.bearhive.bearhive.Service.UserCourseService;

@ControllerAdvice
public class GlobalControllerAdvice {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCourseService userCourseService;

    @ModelAttribute
    public void addUserDetails(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && 
            authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal().toString())) {
                
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetail) {
                CustomUserDetail userDetails = (CustomUserDetail) principal;
                model.addAttribute("fullname", userDetails.getFullname());

                User user = userRepository.findByEmail(userDetails.getUsername())
                        .orElse(null);
                if (user != null) {
                    Long cartSize = userCourseService.countCartItems(user.getId(), "CART");
                    model.addAttribute("cartSize", cartSize);
                } else {
                    model.addAttribute("cartSize", 0L);
                }
            }
        } else {
            model.addAttribute("cartSize", 0L);
        }
    }
}
