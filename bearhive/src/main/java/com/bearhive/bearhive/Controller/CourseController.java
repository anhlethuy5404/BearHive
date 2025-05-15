package com.bearhive.bearhive.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bearhive.bearhive.Model.Course;
import com.bearhive.bearhive.Service.CourseService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CourseController {
    @Autowired
    private CourseService courseService;

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
}
