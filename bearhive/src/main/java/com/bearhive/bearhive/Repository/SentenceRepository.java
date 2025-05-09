package com.bearhive.bearhive.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.Sentence;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long>{
    List<Sentence> findByDictationIdOrderByOrderNumber(Long dictationId); 
}
