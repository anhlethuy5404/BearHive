package com.bearhive.bearhive.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Model.UserCourse;
import com.bearhive.bearhive.Repository.UserCourseRepository;
import com.bearhive.bearhive.Repository.UserRepository;

@Service
public class UserCourseService {
    @Autowired
    private UserCourseRepository userCourseRepository;
    @Autowired
    private UserRepository userRepository;

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

    public List<UserCourse> findByUserIdAndCourseIdsAndStatus(Long userId, List<Long> courseIds, String status) {
        return userCourseRepository.findByUserIdAndCourseIdInAndStatus(userId, courseIds, status);
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

    public Long getUserIdFromPrincipal(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        String username = principal.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        return user.getId();
    }
}
