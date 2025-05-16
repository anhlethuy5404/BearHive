package com.bearhive.bearhive.Controller;

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

import com.bearhive.bearhive.Model.Course;
import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Model.UserCourse;
import com.bearhive.bearhive.Repository.UserRepository;
import com.bearhive.bearhive.Service.CourseService;
import com.bearhive.bearhive.Service.UserCourseService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCourseService userCourseService;

    @GetMapping("/course")
    public String showCourse (Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "course";
    }

    @GetMapping("/course/{id}")
    public String showCourseDetail(@PathVariable Long id, Model model, HttpSession session) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course ID"));
        model.addAttribute("course", course);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated() && 
                           !"anonymousUser".equals(authentication.getPrincipal());
        if (!isLoggedIn) {
            session.setAttribute("redirectAfterLogin", "/course/" + id);
        }
        model.addAttribute("isLoggedIn", isLoggedIn);
        return "course-detail";
    }

    @PostMapping("/course/{id}/add-to-cart")
    public String addToCart(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            session.setAttribute("redirectAfterLogin", "/course/" + id);
            return "redirect:/login";
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user email"));
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course ID"));
        
        if (userCourseService.findByUserIdAndCourseIdAndStatus(user.getId(), id, "CART").isPresent()) {
            redirectAttributes.addFlashAttribute("message", "Course is already in your cart!");
            return "redirect:/course/" + id;
        }
        if (userCourseService.findByUserIdAndCourseIdAndStatus(user.getId(), id, "PURCHASED").isPresent()) {
            redirectAttributes.addFlashAttribute("message", "You have already purchased this course!");
            return "redirect:/course/" + id;
        }

        UserCourse userCourse = new UserCourse();
        userCourse.setUser(user);
        userCourse.setCourse(course);
        userCourse.setStatus("CART");
        userCourseService.save(userCourse);
        redirectAttributes.addFlashAttribute("message", "Course added to cart successfully!");

        Long cartSize = userCourseService.countCartItems(user.getId(), "CART");
        session.setAttribute("cartSize", cartSize);
        return "redirect:/course/" + id;
    }
}
