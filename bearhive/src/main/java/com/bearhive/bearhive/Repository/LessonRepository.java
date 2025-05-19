package com.bearhive.bearhive.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long>{
    List<Lesson> findByCourseModuleId(Long courseModuleId);
}
