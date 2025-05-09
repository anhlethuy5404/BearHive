package com.bearhive.bearhive.Model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_test")
public class UserTest {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id")
    private Test test;

    private int score;
    private LocalDate completedDate;
    private int timeTaken; 

    @OneToMany(mappedBy = "userTest", fetch = FetchType.LAZY, cascade = jakarta.persistence.CascadeType.ALL)
    private List<UserAnswer> userAnswers;

    public UserTest(User user, Test test, int score, LocalDate completedDate, int timeTaken) {
        this.user = user;
        this.test = test;
        this.score = score;
        this.completedDate = completedDate;
        this.timeTaken = timeTaken;
    }
}
