package com.bearhive.bearhive.Model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
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
@Table(name = "dictation")
public class Dictation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title, description, type, difficulty, coveredImage, audio;
    private LocalDate createdDate;
    private LocalDate updateDate;
    private int learners;
    private double rating;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDate.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy="dictation", fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    private List<UserDictation> userDictations;

    @OneToMany(mappedBy="dictation", fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    private List<Sentence> sentences;

    public Dictation(String title, String description, String type, String difficulty, String coveredImage, String audio, int learners, double rating, LocalDate updateDate, User user) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.difficulty = difficulty;
        this.coveredImage = coveredImage;
        this.audio = audio;
        this.learners = learners;
        this.rating = rating;
        this.updateDate = updateDate;
        this.user = user;
    }
}
