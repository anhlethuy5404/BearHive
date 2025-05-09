package com.bearhive.bearhive.Model;

import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("FILL_IN_BLANK")
@Getter
@Setter
@NoArgsConstructor
public class FillInBlankQuestion extends Question{
    @ElementCollection
    private List<String> correctAnswers;

    private Integer maxWords;

    public FillInBlankQuestion(String questionText, List<String> correctAnswers, Integer maxWords) {
        super.setQuestionText(questionText);
        this.correctAnswers = correctAnswers;
        this.maxWords = maxWords;
    }

    @Override
    public String getQuestionType() {
        return "FILL_IN_BLANK";
    }
}
