package com.bearhive.bearhive.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bearhive.bearhive.Model.Lesson;
import com.bearhive.bearhive.Service.LessonService;

@Controller
public class LessonController {
    @Autowired
    private LessonService lessonService;

    @GetMapping("/lesson/{id}")
    public String showLessonDetail(@PathVariable Long id, Model model) {
        Lesson lesson = lessonService.getLessonById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid lesson ID"));
        model.addAttribute("lesson", lesson);
        return "course-content";
    }
}
