package com.bearhive.bearhive.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bearhive.bearhive.Model.Lesson;
import com.bearhive.bearhive.Repository.LessonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LessonService {
    @Autowired
    private LessonRepository lessonRepository;

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }
    public Optional<Lesson> getLessonById(Long id) {
        return lessonRepository.findById(id);
    }
}
