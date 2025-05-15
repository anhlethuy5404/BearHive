package com.bearhive.bearhive.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long>{

}
