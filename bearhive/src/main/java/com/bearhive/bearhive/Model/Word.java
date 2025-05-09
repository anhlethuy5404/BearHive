package com.bearhive.bearhive.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "word")
public class Word {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String word, pronunciation, audio, type, meaning, example, image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcard_id")
    private Flashcard flashcard;

    public Word(String word, String pronunciation, String audio, String type, String meaning, String example, String image, Flashcard flashcard) {
        this.word = word;
        this.pronunciation = pronunciation;
        this.audio = audio;
        this.type = type;
        this.meaning = meaning;
        this.example = example;
        this.image = image;
        this.flashcard = flashcard;
    }
}
