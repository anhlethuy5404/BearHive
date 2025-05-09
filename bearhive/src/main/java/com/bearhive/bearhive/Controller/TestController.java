package com.bearhive.bearhive.Controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.bearhive.bearhive.Model.FillInBlankQuestion;
import com.bearhive.bearhive.Model.MultipleChoiceQuestion;
import com.bearhive.bearhive.Model.Question;
import com.bearhive.bearhive.Model.Test;
import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Model.UserAnswer;
import com.bearhive.bearhive.Model.UserTest;
import com.bearhive.bearhive.Repository.QuestionRepository;
import com.bearhive.bearhive.Repository.UserRepository;
import com.bearhive.bearhive.Repository.UserTestRepository;
import com.bearhive.bearhive.Service.TestService;

import jakarta.servlet.http.HttpSession;



@Controller
public class TestController {
    @Autowired
    private TestService testService; 

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTestRepository userTestRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/test")
    public String test(Model model) {
        model.addAttribute("tests", testService.getAllTests());
        return "test";
    }

    @GetMapping("/ielts/{id}")
    public String getTestInfo(@PathVariable Long id, Model model, HttpSession session) {
        Test test = testService.getTestById(id);
        model.addAttribute("test", test);
        model.addAttribute("parts", test.getParts());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated() && 
                           !"anonymousUser".equals(authentication.getPrincipal());
        if (!isLoggedIn) {
            session.setAttribute("redirectAfterLogin", "/ielts/" + id);
        }
        model.addAttribute("isLoggedIn", isLoggedIn);
        return "ielts";
    }

    @GetMapping("/ielts/test/{id}")
    public String getTestContent(@PathVariable Long id, Model model, HttpSession session) {
        Test test = testService.getTestById(id);
        model.addAttribute("test", test);
        model.addAttribute("parts", test.getParts());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated() && 
                           !"anonymousUser".equals(authentication.getPrincipal());
        if (!isLoggedIn) {
            session.setAttribute("redirectAfterLogin", "/ielts/test/" + id);
        }
        model.addAttribute("isLoggedIn", isLoggedIn);
        return "ielts-test";
    }
    
    @GetMapping("/ielts/test/{id}/result")
    public String getMethodName(@PathVariable Long id, Model model) {
        Test test = testService.getTestById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        UserTest userTest = null;
        if (user != null) {
            userTest = userTestRepository.findAll().stream()
                    .filter(ut -> ut.getTest().getId().equals(id) && ut.getUser().getId().equals(user.getId()))
                    .max((ut1, ut2) -> ut1.getCompletedDate().compareTo(ut2.getCompletedDate()))
                    .orElse(null);
        }

        model.addAttribute("test", test);
        model.addAttribute("parts", test.getParts());
        model.addAttribute("userTest", userTest);
        return "test-result";
    }
    
    @PostMapping("/ielts/test/submit/{testId}")
    public String submitTest(@PathVariable Long testId,
        @RequestBody Map<Long, String> userAnswers,
        @RequestParam int timeTaken , Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        Test test = testService.getTestById(testId);
        UserTest userTest = new UserTest();
        userTest.setUser(user);
        userTest.setTest(test);
        userTest.setCompletedDate(LocalDate.now());
        userTest.setTimeTaken(timeTaken);
        List<UserAnswer> userAnswerList = new ArrayList<>();
        int score = 0;
        for (Map.Entry<Long, String> entry : userAnswers.entrySet()) {
            Long questionId = entry.getKey();
            String userAnswer = entry.getValue();

            Question question = questionRepository.findById(questionId).orElse(null);
            if (question == null) continue;

            boolean isCorrect = false;
            UserAnswer userAnswerEntity = new UserAnswer();
            userAnswerEntity.setUserTest(userTest);
            userAnswerEntity.setQuestion(question);
            userAnswerEntity.setAnswerText(userAnswer);

            if (question instanceof MultipleChoiceQuestion) {
                String correctLetter = question.getCorrectAnswer();
                isCorrect = userAnswer.equals(correctLetter);
            } else if (question instanceof FillInBlankQuestion fibq) {
                isCorrect = fibq.getCorrectAnswers().stream()
                        .anyMatch(correct -> correct.trim().equalsIgnoreCase(userAnswer.trim()));
            }

            userAnswerEntity.setCorrect(isCorrect);
            userAnswerList.add(userAnswerEntity);
            if (isCorrect) score++;
        }

        userTest.setScore(score);
        userTest.setUserAnswers(userAnswerList);

        // Lưu vào database
        userTestRepository.save(userTest);
        
        return "redirect:/ielts/test/" + testId + "/result";
    }
}
