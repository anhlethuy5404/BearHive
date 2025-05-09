package com.bearhive.bearhive.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>{
    List<Question> findByGeneralQuestionId(Long generalQuestionId);
}
