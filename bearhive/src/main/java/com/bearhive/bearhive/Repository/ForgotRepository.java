package com.bearhive.bearhive.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.Forgot;

@Repository
public interface ForgotRepository extends JpaRepository<Forgot, Long> {
    Forgot findByUserEmail(String email);
}
