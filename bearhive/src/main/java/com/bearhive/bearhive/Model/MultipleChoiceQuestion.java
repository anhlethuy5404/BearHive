package com.bearhive.bearhive.Model;

import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
@Getter
@Setter
@NoArgsConstructor
public class MultipleChoiceQuestion extends Question{
    @ElementCollection
    private List<String> options;  
    private String correctAnswer;

    public MultipleChoiceQuestion(String questionText, List<String> options, String correctAnswer) {
        super.setQuestionText(questionText);
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    @Override
    public String getQuestionType() {
        return "MULTIPLE_CHOICE";
    }

    @Override
    public String getCorrectAnswer() {
        return super.getCorrectAnswer();
    }
}
