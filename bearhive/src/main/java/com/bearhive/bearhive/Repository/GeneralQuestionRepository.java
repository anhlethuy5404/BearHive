package com.bearhive.bearhive.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.GeneralQuestion;

@Repository
public interface GeneralQuestionRepository extends JpaRepository<GeneralQuestion, Long> {
    List<GeneralQuestion> findByPartId(Long partId);
}
