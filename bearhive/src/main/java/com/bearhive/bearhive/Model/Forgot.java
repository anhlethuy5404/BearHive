package com.bearhive.bearhive.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "forgot")
public class Forgot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fgid;

    private String otp;
    private LocalDateTime expiredDate;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Forgot(String otp, User user, LocalDateTime expiredDate) {
        this.otp = otp;
        this.user = user;
        this.expiredDate = expiredDate;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredDate);
    }
}
