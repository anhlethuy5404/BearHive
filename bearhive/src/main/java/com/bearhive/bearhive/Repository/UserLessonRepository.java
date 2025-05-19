package com.bearhive.bearhive.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.UserLesson;

@Repository
public interface UserLessonRepository extends JpaRepository<UserLesson, Long>{
    Optional<UserLesson> findByUserIdAndLessonId(Long userId, Long lessonId);
    List<UserLesson> findByUserIdAndLessonCourseModuleId(Long userId, Long courseModuleId);
    Long countByLessonIdAndCompletedTrue(Long lessonId);
}
