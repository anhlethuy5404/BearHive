package com.bearhive.bearhive.Model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "flashcard")
public class Flashcard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title, difficulty, type, description, coveredImage;
    private int questions;
    private LocalDate createdDate;
    private LocalDate updateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "flashcard", fetch = FetchType.LAZY, cascade = jakarta.persistence.CascadeType.ALL)
    private List<Word> words;

    @OneToMany(mappedBy = "flashcard", fetch = FetchType.LAZY, cascade = jakarta.persistence.CascadeType.ALL)
    private List<UserFlashcard> userFlashcards;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDate.now();
    }

    public Flashcard(String title, String difficulty, String type, String description, String coveredImage, int questions, LocalDate updateDate, User user) {
        this.title = title;
        this.difficulty = difficulty;
        this.type = type;
        this.description = description;
        this.coveredImage = coveredImage;
        this.questions = questions;
        this.updateDate = updateDate;
        this.user = user;
    }
}
