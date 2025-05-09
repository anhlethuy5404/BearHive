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
@Table (name = "sentence")
public class Sentence {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String text, audio, hint;
    private int orderNumber;
    private String vietTrans;

    @ManyToOne(fetch=jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name="dictation_id")
    private Dictation dictation;

    public Sentence(String text, String audio, String hint, int orderNumber, String vietTrans) {
        this.text = text;
        this.audio = audio;
        this.hint = hint;
        this.orderNumber = orderNumber;
        this.vietTrans = vietTrans;
    }
}
