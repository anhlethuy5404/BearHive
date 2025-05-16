package com.bearhive.bearhive.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bearhive.bearhive.Model.UserCourse;
import com.bearhive.bearhive.Repository.UserCourseRepository;

@Service
public class UserCourseService {
    @Autowired
    private UserCourseRepository userCourseRepository;

    public void save(UserCourse userCourse) {
        userCourseRepository.save(userCourse);
    }

    public Optional<UserCourse> findById(Long id) {
        return userCourseRepository.findById(id);
    }

    public List<UserCourse> findByUserIdAndStatus(Long userId, String status) {
        return userCourseRepository.findByUserIdAndStatus(userId, status);
    }

    public Optional<UserCourse> findByUserIdAndCourseIdAndStatus(Long userId, Long courseId, String status) {
        return userCourseRepository.findByUserIdAndCourseIdAndStatus(userId, courseId, status);
    }

    public long countCartItems(Long userId, String status) {
        return userCourseRepository.countByUserIdAndStatus(userId, status);
    }

    public void delete(UserCourse userCourse) {
        userCourseRepository.delete(userCourse);
    }

    public void deleteByUserIdAndCourseId(Long userId, Long courseId) {
        userCourseRepository.deleteByUserIdAndCourseId(userId, courseId);
    }
}
