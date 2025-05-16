package com.bearhive.bearhive.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.UserCourse;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Long>{
    List<UserCourse> findByUserIdAndStatus(Long userId, String status);
    Optional<UserCourse> findByUserIdAndCourseIdAndStatus(Long userId, Long courseId, String status);
    long countByUserIdAndStatus(Long userId, String status);
    void deleteByUserIdAndCourseId(Long userId, Long courseId);
    List<UserCourse> findByUserIdAndCourseIdInAndStatus(Long userId, List<Long> courseIds, String status);
}
