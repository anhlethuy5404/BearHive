package com.bearhive.bearhive.Controller;

import java.util.List;
import java.util.Optional;

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
import com.bearhive.bearhive.Model.CourseModule;
import com.bearhive.bearhive.Model.Lesson;
import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Model.UserCourse;
import com.bearhive.bearhive.Model.UserLesson;
import com.bearhive.bearhive.Repository.CourseModuleRepository;
import com.bearhive.bearhive.Repository.CourseRepository;
import com.bearhive.bearhive.Repository.LessonRepository;
import com.bearhive.bearhive.Repository.UserLessonRepository;
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
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseModuleRepository courseModuleRepository;
    @Autowired
    private UserLessonRepository userLessonRepository;

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
        UserCourse userCourse = null;
        if (isLoggedIn) {
            Optional<User> userOpt = userRepository.findByEmail(authentication.getName());
            if (userOpt.isPresent()) {
                userCourse = userCourseService.findByUserIdAndCourseIdAndStatus(
                    userOpt.get().getId(), id, "PURCHASED"
                ).orElse(null);
            }
        }
        model.addAttribute("userCourse", userCourse);
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

    @GetMapping("/mycourse/{id}")
    public String showMyCourse(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user email"));
        List<UserCourse> userCourses = userCourseService.findByUserIdAndStatus(user.getId(), "PURCHASED");
        List<Course> courses = userCourses.stream()
                .map(UserCourse::getCourse)
                .toList();
        model.addAttribute("user", user);
        model.addAttribute("purchasedCourses", courses);
        return "mycourse";
    }
    
    @GetMapping("/course/content/{id}")
    public String showCourseContent(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user email"));
        // Kiểm tra quyền sở hữu khóa học
        if (userCourseService.findByUserIdAndCourseIdAndStatus(user.getId(), id, "PURCHASED").isEmpty()) {
            return "redirect:/course/" + id;
        }
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course ID"));
        long totalLessons = 0;
        long completedCount = 0;
        long totalDuration = 0;
        for (CourseModule module : course.getCourseModules()) {
            List<Lesson> lessons = module.getLessons();
            totalLessons += lessons.size();
            totalDuration += module.getDuration();
            for (Lesson lesson : lessons) {
                if (userLessonRepository.findByUserIdAndLessonId(user.getId(), lesson.getId())
                    .map(UserLesson::getCompleted).orElse(false)) {
                    completedCount++;
                }
            }
        }
        model.addAttribute("user", user);
        model.addAttribute("course", course);
        model.addAttribute("modules", course.getCourseModules());
        long progress = totalLessons > 0 ? (completedCount * 100) / totalLessons : 0;
        model.addAttribute("progress", progress);
        model.addAttribute("moduleCount", course.getCourseModules().size());
        model.addAttribute("lessonCount", totalLessons);
        model.addAttribute("totalDuration", totalDuration);
        return "course-module";
    }
    
    @GetMapping("/course/{courseId}/lessons/{lessonId}")
    public String getLessonPage(@PathVariable Long courseId, @PathVariable Long lessonId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user email"));
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        CourseModule module = lesson.getCourseModule();
        List<Lesson> moduleLessons = lessonRepository.findByCourseModuleId(module.getId());
        List<UserLesson> userLessons = userLessonRepository.findByUserIdAndLessonCourseModuleId(user.getId(), module.getId());
        
        long completedLessons = userLessons.stream().filter(UserLesson::getCompleted).count();
        long totalLessons = moduleLessons.size();
        int progressPercentage = totalLessons > 0 ? (int) ((completedLessons * 100) / totalLessons) : 0;
        for (Lesson modLesson : moduleLessons) {
            modLesson.setCompletedCount(userLessonRepository.countByLessonIdAndCompletedTrue(modLesson.getId()));
        }
        
        model.addAttribute("user", user);
        model.addAttribute("course", course);
        model.addAttribute("lesson", lesson);
        model.addAttribute("module", module);
        model.addAttribute("moduleLessons", moduleLessons);
        model.addAttribute("completedLessons", completedLessons);
        model.addAttribute("totalLessons", totalLessons);
        model.addAttribute("progressPercentage", progressPercentage);

        int currentLessonIndex = moduleLessons.indexOf(lesson);
        Lesson previousLesson = currentLessonIndex > 0 ? moduleLessons.get(currentLessonIndex - 1) : null;
        Lesson nextLesson = currentLessonIndex < moduleLessons.size() - 1 ? moduleLessons.get(currentLessonIndex + 1) : null;
        model.addAttribute("currentLessonIndex", currentLessonIndex);
        model.addAttribute("previousLesson", previousLesson);
        model.addAttribute("nextLesson", nextLesson);

        int currentModuleIndex = course.getCourseModules().indexOf(module);
        model.addAttribute("currentModuleIndex", currentModuleIndex);
        return "course-content"; 
    }

    @PostMapping("/course/{courseId}/lessons/{lessonId}/complete")
    public String markLessonCompleted(@PathVariable Long courseId, @PathVariable Long lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user email"));
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));
        Optional<UserLesson> userLessonOpt = userLessonRepository.findByUserIdAndLessonId(user.getId(), lessonId);
        UserLesson userLesson;
        if (userLessonOpt.isPresent()) {
            userLesson = userLessonOpt.get();
            userLesson.setCompleted(true);
        } else {
            userLesson = new UserLesson();
            userLesson.setUser(user);
            userLesson.setLesson(lesson);
            userLesson.setCompleted(true);
        }
        userLessonRepository.save(userLesson);
        lesson.setCompletedCount(userLessonRepository.countByLessonIdAndCompletedTrue(lessonId));
        lessonRepository.save(lesson);
        return "redirect:/course/" + courseId + "/lessons/" + lessonId;
    }
}
