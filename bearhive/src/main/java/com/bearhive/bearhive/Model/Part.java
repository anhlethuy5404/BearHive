package com.bearhive.bearhive.Model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
@Table(name = "part")
public class Part {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private int number;
    private String title;//type : reading/listening
    private String audio;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    private Test test;

    @OneToMany(mappedBy = "part", fetch = jakarta.persistence.FetchType.LAZY, cascade = jakarta.persistence.CascadeType.ALL)
    private List<GeneralQuestion> generalQuestions;

    public Part(int number, String title, String audio) {
        this.number = number;
        this.title = title;
        this.audio = audio;
    }
}
