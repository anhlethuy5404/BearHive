package com.bearhive.bearhive.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.Word;

@Repository
public interface WordRepository extends JpaRepository<Word, Long>{
    List<Word> findByFlashcardId(Long flashcardId);
}
