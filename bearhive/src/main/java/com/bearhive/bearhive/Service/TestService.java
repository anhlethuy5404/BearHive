package com.bearhive.bearhive.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bearhive.bearhive.Model.Test;
import com.bearhive.bearhive.Model.UserAnswer;
import com.bearhive.bearhive.Model.UserTest;
import com.bearhive.bearhive.Repository.TestRepository;

@Service
public class TestService {
    @Autowired
    private TestRepository testRepository;

    public List<Test> getAllTests() {
        return testRepository.findAll();
    }
    
    public Test getTestById(Long id) {
        Test test = testRepository.findById(id).orElseThrow(() -> new RuntimeException("Test not found"));
        test.getParts().forEach(part -> {
            part.getGeneralQuestions().forEach(gq -> {
                gq.getQuestions().size(); 
            });
        });
        return test;
    }

    public Map<Long, UserAnswer> getUserAnswersMap(UserTest userTest) {
        Map<Long, UserAnswer> userAnswerMap = new HashMap<>();
        if (userTest != null && userTest.getUserAnswers() != null) {
            for (UserAnswer userAnswer : userTest.getUserAnswers()) {
                if (userAnswer.getQuestion() != null) {
                    userAnswerMap.put(userAnswer.getQuestion().getId(), userAnswer);
                }
            }
        }
        return userAnswerMap;
    }
}
