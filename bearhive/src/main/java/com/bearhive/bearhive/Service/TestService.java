package com.bearhive.bearhive.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bearhive.bearhive.Model.Test;
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
}
