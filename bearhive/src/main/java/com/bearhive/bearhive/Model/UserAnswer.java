package com.bearhive.bearhive.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table (name = "user_answer")
public class UserAnswer {
    @Id
    @GeneratedValue (strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne (fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "user_test_id")
    private UserTest userTest;

    @ManyToOne (fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    private String answerText;
    private boolean isCorrect;

    public UserAnswer(UserTest userTest, Question question, String answerText, boolean isCorrect) {
        this.userTest = userTest;
        this.question = question;
        this.answerText = answerText;
        this.isCorrect = isCorrect;
    }
}
